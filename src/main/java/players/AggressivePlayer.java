package players;

import attacks.Attack;
import attacks.PlayerAttack;
import game.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AggressivePlayer extends Player {

    public AggressivePlayer(String name) {
        super(name);
    }

    public AggressivePlayer(){}

    public AggressivePlayer(Player other) {
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
        AggressivePlayer res = new AggressivePlayer();
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
        List<Attack> attacksOnPlayer = new ArrayList<>();
        int opponentPlayersHp = opponent.getHp();
        for (int i=0; i<warriors.size(); i++){
            Card warrior = warriors.get(i);
            Attack attack = new PlayerAttack(warrior, opponent);
            attacksOnPlayer.add(attack);
            opponentPlayersHp -= warrior.getAttack();
            if (opponentPlayersHp <= 0)
                i = warriors.size();  // if player dies, return to the game
        }
        return Collections.singletonList(attacksOnPlayer);
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
