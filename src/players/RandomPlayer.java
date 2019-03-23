package players;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;

import java.util.*;

public class RandomPlayer extends Player {

    private static Random random = new Random();

    private RandomPlayer() {

    }

    public RandomPlayer(String name) {
        super(name);
    }

    @Override
    public Player deepCopy() {
        RandomPlayer res = new RandomPlayer();
        return res.modify(res);
    }

    public RandomPlayer(RandomPlayer other) {
        super(other);
    }


    /* Generate one random attack */
    @Override
    public List<List<Attack>> getPossibleAttacks(Player opponent) {
        /* Prepare copy of the game */
        Player opponentsPlayer = new PlayerToAttack(opponent.name, opponent.hp);
        List<Card> opponentsWarriors = new ArrayList<>();
        List<Card> realOpponentsWarriors = opponent.getWarriors();
        for (Card realOpponentsWarrior : realOpponentsWarriors)
            opponentsWarriors.add(new Card(realOpponentsWarrior));

        /* Simulate random attacks */
        List<Attack> selectedAttacks = new ArrayList<>();
        for (int i=0; i<warriors.size(); i++) {
            Card attackingWarrior = warriors.get(i);
            Attack attack;
            if (random.nextInt(opponentsWarriors.size() + 1) == 0) {  // attack on opponents Player
                attack = new PlayerAttack(attackingWarrior, opponent);
                opponentsPlayer.setHp(opponentsPlayer.getHp() - attackingWarrior.getAttack());
                if (attack.targetDies())
                    i = warriors.size();
            }
            else {  // attack on opponents other warrior
                int targetOpponentIndex = random.nextInt(opponentsWarriors.size());
                Card targetOpponent = opponentsWarriors.get(targetOpponentIndex);
                Card copyOfTargetOpponent = new Card(targetOpponent);
                attack = new WarriorAttack(attackingWarrior, copyOfTargetOpponent);
                if (attack.targetDies())
                    opponentsWarriors.remove(targetOpponent);
                else
                    targetOpponent.setHp(targetOpponent.getHp() - attackingWarrior.getAttack());
            }
            selectedAttacks.add(attack);
        }
        return Collections.singletonList(selectedAttacks);
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        if (!possibleCardsToPlay.isEmpty())
            return possibleCardsToPlay.get(random.nextInt(possibleCardsToPlay.size()));
        else
            return new ArrayList<>();
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        if (!possibleAttacks.isEmpty())
            return possibleAttacks.get(0);
        else
            return new ArrayList<>();
    }


}
