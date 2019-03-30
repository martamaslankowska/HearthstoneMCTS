package players;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;

import java.util.*;
import java.util.stream.Collectors;

public class ControllingPlayer extends Player {

    public ControllingPlayer(String name) {
        super(name);
    }

    public ControllingPlayer(){}

    public ControllingPlayer(Player other) {
        this.name = other.name;
        this.mana = other.mana;
        this.hp = other.hp;
        this.punishment = other.punishment;

        this.deck = new ArrayList<>();
        for (Card card : other.deck)
            this.deck.add(new Card(card));

        this.hand = new ArrayList<>();
        for (Card card : other.hand)
            this.hand.add(new Card(card));

        this.warriors = new ArrayList<>();
        for (Card card : other.warriors)
            this.warriors.add(new Card(card));
    }

    @Override
    public Player deepCopy() {
        ControllingPlayer res = new ControllingPlayer();
        res.name = name;
        res.hp = hp;
        res.punishment = punishment;
        res.mana = mana;

        res.deck = new ArrayList<>();
        for (Card card : deck)
            res.deck.add(new Card(card));

        res.hand = new ArrayList<>();
        for (Card card : hand)
            res.hand.add(new Card(card));

        res.warriors = new ArrayList<>();
        for (Card card : warriors)
            res.warriors.add(new Card(card));

        return res;
    }


    public <E> List<List<E>> generatePerm(List<E> original) {
        if (original.size() == 0) {
            List<List<E>> result = new ArrayList<List<E>>();
            result.add(new ArrayList<E>());
            return result;
        }
        E firstElement = original.remove(0);
        List<List<E>> returnValue = new ArrayList<List<E>>();
        List<List<E>> permutations = generatePerm(original);
        for (List<E> smallerPermutated : permutations) {
            for (int index=0; index <= smallerPermutated.size(); index++) {
                List<E> temp = new ArrayList<E>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }

    @Override
    public List<List<Attack>> getPossibleAttacks(Player opponent, int move) {
        List<Attack> controllingAttacks = new ArrayList<>();
        int globalDeadWarriors=0;
        int globalTotalDamage=0;
        Player myCopy=this.deepCopy();
        List<List<Card>> orderPermutations=generatePerm(myCopy.warriors);
        for (List<Card> permutation:orderPermutations
             ) {
            Player copyOfOpponent = opponent.deepCopy();
            List<Attack> permutationAttack=new ArrayList<>();
            int totalDeadWarriors=0;
            int totalDamage=0;
            Attack attack;
            for (int i = 0; i < permutation.size(); i++) {
                Card warrior = permutation.get(i);
                if (!copyOfOpponent.getWarriors().isEmpty()) {  // WarriorAttack
                    ControllingResult controllingResult = getOpponentWariorMaximizingHpDifference(warrior, copyOfOpponent.warriors);
                    Card target = controllingResult.target;
                    totalDamage += controllingResult.difference;
                    if (target.getHp() <=0) {
                        copyOfOpponent.warriors.remove(target);
                        totalDeadWarriors++;
                    }
                    attack = new WarriorAttack(warrior, opponent.getWarriors().get(opponent.getWarriors().indexOf((Card)target)));
                } else {
                    attack = new PlayerAttack(warrior, copyOfOpponent);
                }
                permutationAttack.add(attack);
            }
            if(totalDeadWarriors>globalDeadWarriors)
            {
                globalDeadWarriors=totalDeadWarriors;
                globalTotalDamage=totalDamage;
                controllingAttacks=permutationAttack;
            }
            if(totalDeadWarriors==globalDeadWarriors&&totalDamage>=globalTotalDamage){
                globalDeadWarriors=totalDeadWarriors;
                globalTotalDamage=totalDamage;
                controllingAttacks=permutationAttack;
            }
            else if(totalDamage<globalTotalDamage&&permutationAttack.size()==1)
            {
                globalDeadWarriors=totalDeadWarriors;
                globalTotalDamage=totalDamage;
                controllingAttacks=permutationAttack;
            }
        }
//        System.out.println("CONTR"+controllingAttacks);
        return Collections.singletonList(controllingAttacks);
    }

    private ControllingResult getOpponentWariorMaximizingHpDifference(Card myWarior, List<Card> targets){
        int difference=-1000;
        int chosenLoss=1000;
        int chosenDamage=0;
        Card chosenTarget=null;
        int size=targets.size();
        Card killable=getKillableOponentsWarrior(myWarior,targets);
        if(killable!=null)
        {
            int killableHp=killable.getHp();
            killable.setHp(killableHp-myWarior.getAttack());
            return new ControllingResult(killable,killableHp-killable.getAttack());
        }
        for (int i=0;i<size;i++){
            Card target=targets.get(i);
            int damage=myWarior.getAttack()>target.getHp()?target.getHp():myWarior.getAttack();
            int loss=target.getAttack()>myWarior.getHp()?myWarior.getHp():target.getAttack();
            int currentDifference=damage-loss;
            if(currentDifference>difference){
                chosenTarget=target;
                difference=currentDifference;
                chosenLoss=loss;
                chosenDamage=damage;
            }
            if(currentDifference==difference&&loss<chosenLoss)
            {
                chosenTarget=target;
                difference=currentDifference;
                chosenLoss=loss;
                chosenDamage=damage;
            }
        }
        chosenTarget.setHp(chosenTarget.getHp()-chosenDamage);
        return new ControllingResult(chosenTarget,difference);
    }

    private Card getKillableOponentsWarrior(Card myWarior, List<Card> targets){
        Card killable=null;
        int loss=1000;
        int size=targets.size();
        for (int i=0;i<size;i++){
            if(myWarior.getAttack()>=targets.get(i).getHp()&&targets.get(i).getAttack()<loss){
                loss=targets.get(i).getAttack();
                killable=targets.get(i);
            }
        }
        return killable;
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        List<Card> cardsWithGreatestHp = new ArrayList<>();
        int greatestHpPower = 0;
        for (List<Card> cards : possibleCardsToPlay) {
            int overallHpPower = 0;
            for (Card warrior : cards)
                overallHpPower += warrior.getHp();
            if (overallHpPower > greatestHpPower) {
                cardsWithGreatestHp = cards;
                greatestHpPower = overallHpPower;
            }
        }
        return cardsWithGreatestHp;
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        return possibleAttacks.get(0);
    }
}
