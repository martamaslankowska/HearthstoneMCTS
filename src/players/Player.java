package players;

import attacks.Attack;
import attacks.PlayerAttack;
import game.Card;
import attacks.ITargetWhileAttack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static game.Main.CARDS;


public abstract class Player implements ITargetWhileAttack {

    protected String name;
    protected int hp;
    protected int punishment;
    protected int mana;
    protected List<Card> deck;
    protected List<Card> hand;
    protected List<Card> warriors;

    protected Player() {}

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
        throw new NotImplementedException();
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

    public abstract Player deepCopy();

    protected Player modify(Player res) {
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
        else {
            hp -= ++punishment;
//            System.out.println("  - HP: " + punishment);
        }
    }

    public List<List<Card>> getPossibleCardsToPlay() {
        /* Finding suitable pairs of cards to play */
        List<List<Card>> possibleCards = new ArrayList<>();
        for(int i=0; i<hand.size()-1; i++) {
            List<Card> subHand = hand.subList(i+1, hand.size());
            for(int j=0; j<subHand.size(); j++) {
                if (hand.get(i).getMana() + subHand.get(j).getMana() <= mana) {
                    Card firstCard = hand.get(i);
                    Card secondCard = hand.get(i+j+1);
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

    public abstract List<List<Attack>> getPossibleAttacks(Player opponent);

    public abstract List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay);

    public abstract List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks);

    public void attackOpponentsCards(Player opponent, List<Attack> selectedAttacks, boolean verbose) {
        if (verbose)
            if (selectedAttacks.isEmpty())
                System.out.println("    NO ATTACKS");
            else
                System.out.println("    ATTACKS:");

        for (int i=0; i<selectedAttacks.size(); i++) {
            Attack selectedAttack = selectedAttacks.get(i);
            if (verbose)
                System.out.println("      " + selectedAttack);

            if (selectedAttack instanceof PlayerAttack) {  // PlayerAttack type
                opponent.setHp(opponent.getHp() - selectedAttack.getAttacker().getAttack());
            }
            else {  // WarriorAttack type of attack

                Card warrior = warriors.get(warriors.indexOf(selectedAttack.getAttacker()));
                Card opponentsWarrior = opponent.getWarriors().get(opponent.getWarriors().indexOf((Card)selectedAttack.getTarget()));

                if (selectedAttack.attackerDies()){
                    if (verbose)
                        System.out.println("      - ally warrior " + warrior.getName() + " died...");
                    warriors.remove(warrior);
                }
                else
                    warrior.setHp(warrior.getHp() - opponentsWarrior.getAttack());

                if (selectedAttack.targetDies()) {
                    if (verbose)
                        System.out.println("      - opponents warrior " + opponentsWarrior.getName() + " died");
                    opponent.getWarriors().remove(opponentsWarrior);
                }
                else
                    opponentsWarrior.setHp(opponentsWarrior.getHp() - warrior.getAttack());
            }
        }
    }

    public void performSingleAttack(Player opponent, Attack attack) {
        attackOpponentsCards(opponent, Collections.singletonList(attack), false);
    }

    public void playCards(List<Card> selectedCards, boolean verbose) {
        if (verbose)
            System.out.println("    PLAYED CARDS: " + selectedCards);
        if (!selectedCards.isEmpty()) {
            warriors.addAll(selectedCards);
            hand.removeAll(selectedCards);
        }
    }


}
