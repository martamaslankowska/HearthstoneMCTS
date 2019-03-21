package players;

import game.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static game.Main.CARDS;


public abstract class Player {

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
        deck.addAll(new ArrayList<Card>(CARDS));
        deck.addAll(new ArrayList<Card>(CARDS));
        Collections.shuffle(deck);
        for (int i=0; i<deck.size(); i++)
            deck.get(i).setId(i);
    }

    public void hit() {
        if (!deck.isEmpty()) {
            hand.add(deck.get(0));
            deck.remove(0);
        }
        else
            hp -= ++punishment;
    }

    public List<ArrayList<Card>> getPossibleCardsToPlay() {
        /* Finding suitable pairs of cards to play */
        List<ArrayList<Card>> possibleCards = new ArrayList<>();
        for(int i=0; i<hand.size()-1; i++) {
            List<Card> subHand = hand.subList(i+1, hand.size());
            for(int j=0; j<subHand.size(); j++) {
                if (hand.get(i).getMana() + hand.get(j).getMana() <= mana) {
                    Card firstCard = hand.get(i);
                    Card secondCard = hand.get(j);
                    possibleCards.add(new ArrayList<Card>() {{ add(firstCard); add(secondCard); }});
                }
            }
        }

        /* If there is no possible pair of cards to play */
        if (possibleCards.isEmpty()) {
            for(int i=0; i<hand.size()-1; i++) {
                Card card = hand.get(i);
                if (card.getMana() <= mana)
                    possibleCards.add(new ArrayList<Card>() {{ add(card);}});
            }
        }
        return possibleCards;
    }

}
