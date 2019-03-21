package players;

import attacks.Attack;
import game.Card;
import attacks.ITargetWhileAttack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static game.Main.CARDS;


public abstract class Player implements ITargetWhileAttack {

    private String name;
    private int hp;
    private int punishment;
    private int mana;
    private List<Card> deck;
    private List<Card> hand;
    private List<Card> warriors;

    public Player(String name) {
        this.name = name;
        this.hp = 20;
        this.punishment = 0;
        this.mana = 0;
        this.deck = new ArrayList<>();
        this.hand = new ArrayList<>();
        this.warriors = new ArrayList<>();
    }

    public Player(Player other) {
        this.name = other.name;
        this.hp = other.hp;
        this.punishment = other.punishment;
        this.mana = other.mana;
        this.deck = other.deck;
        this.hand = other.hand;
        this.warriors = other.warriors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public List<Card> getWarriors() {
        return warriors;
    }

    public void setWarriors(List<Card> warriors) {
        this.warriors = warriors;
    }


    @Override
    public String toString() {
        return name + " (" + hp + " hp)";
    }


    public void prepareDeck() {
        /* Adding two copies of every card - 20 cards - to the deck */
        for (int i=0; i<CARDS.size()*2; i++)
            deck.add(new Card(CARDS.get(i%CARDS.size()), i));
        Collections.shuffle(deck);
    }

    public void hit() {
        if (!deck.isEmpty()) {
            hand.add(deck.get(0));
            deck.remove(0);
        }
        else
            hp -= ++punishment;
    }

    public List<List<Card>> getPossibleCardsToPlay() {
        /* Finding suitable pairs of cards to play */
        List<List<Card>> possibleCards = new ArrayList<>();
        for(int i=0; i<hand.size()-1; i++) {
            List<Card> subHand = hand.subList(i+1, hand.size());
            for(int j=0; j<subHand.size(); j++) {
                if (hand.get(i).getMana() + hand.get(j).getMana() <= mana) {
                    Card firstCard = hand.get(i);
                    Card secondCard = hand.get(j);
                    possibleCards.add(Arrays.asList(firstCard, secondCard));
                }
            }
        }

        /* If there is no possible pair of cards to play */
        if (possibleCards.isEmpty()) {
            for(int i=0; i<hand.size()-1; i++) {
                Card card = hand.get(i);
                if (card.getMana() <= mana)
                    possibleCards.add(Arrays.asList(card));
            }
        }
        return possibleCards;
    }

    public abstract List<List<Attack>> getPossibleAttacks();

    public abstract List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay);

    public abstract List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks);

    public void attackOpponentsCards(Player opponent, List<Attack> selectedAttacks, boolean verbose) {
        if (verbose)
            System.out.println("  attacks:");

    }

    public void playCards(List<Card> selectedCards, boolean verbose) {
        if (verbose)
            System.out.println("  played cards: " + selectedCards);
        if (!selectedCards.isEmpty()) {
            warriors.addAll(selectedCards);
            hand.removeAll(selectedCards);
        }
    }


}
