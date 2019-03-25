package players;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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



    @Override
    public List<List<Attack>> getPossibleAttacks(Player opponent, int move) {
        Player copyOfOpponent = opponent.deepCopy();
        List<Attack> controllingAttacks = new ArrayList<>();
        Attack attack;
        for (Card warrior : warriors) {
            if (!copyOfOpponent.getWarriors().isEmpty()) {  // WarriorAttack
                // TODO implement possible WarriorAttacks if opponent still has warriors
                System.out.println("Implement this part");
                attack = new WarriorAttack(warrior, copyOfOpponent.getWarriors().get(-1));
            }
            else {  // PlayerAttack (if opponent doesn't have warriors any more)
                attack = new PlayerAttack(warrior, copyOfOpponent);
            }
            controllingAttacks.add(attack);
        }
        return Collections.singletonList(controllingAttacks);
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        List<Card> cardsWithGreatestAttack = new ArrayList<>();
        int greatestAttackPower = 0;
        for (List<Card> cards : possibleCardsToPlay) {
            int overallAttackPower = 0;
            for (Card warrior : cards)
                overallAttackPower += warrior.getAttack();
            if (overallAttackPower > greatestAttackPower) {
                cardsWithGreatestAttack = cards;
                greatestAttackPower = overallAttackPower;
            }
        }
        return cardsWithGreatestAttack;
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        return possibleAttacks.get(0);
    }
}
