package mcts;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String id;
    private int move;
    private Node parentNode;
    private List<Node> childrenExplored;
    private List<Node> childrenUnexplored;
    private int wonPlayouts;
    private int playedPlayouts;
    private Player activePlayer;
    private Player opponentPlayer;
    private Card performedHit;  // hit which result in current node ~ parent: NondeterministicNode
    private Attack performedAttack;  // attack from parent which result in current node
    private List<Card> performedCards;  // list of played cards (1 or 2) by parent which result in current node


    public Node(String id, int move, Node parentNode, Player activePlayer, Player opponentPlayer) {
        this.id = id;
        this.move = move;
        this.parentNode = parentNode;
        this.activePlayer = activePlayer;
        this.opponentPlayer = opponentPlayer;

        this.childrenExplored = new ArrayList<>();
        this.childrenUnexplored = new ArrayList<>();
        this.wonPlayouts = 0;
        this.playedPlayouts = 0;

//        this.activePlayersInactiveWarriors = new ArrayList<>();
        this.performedHit = null;
        this.performedAttack = null;
        this.performedCards = null;
    }

    public Node(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.id = "0";
    }

    public Node(int move, Player activePlayer, Player opponentPlayer) {
        this("0", move, (Node) null, activePlayer, opponentPlayer);
    }

    public Node(String id, int move, Node parentNode, Player activePlayer, Player opponentPlayer, Attack performedAttack) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedAttack = performedAttack;
    }

    public Node(String id, int move, Node parentNode, Player activePlayer, Player opponentPlayer, Attack performedAttack, List<Card> inactiveWarriors) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedAttack = performedAttack;
//        this.activePlayersInactiveWarriors = inactiveWarriors;
    }

    public Node(String id, int move, Node parentNode, Player activePlayer, Player opponentPlayer, List<Card> performedCards) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedCards = performedCards;
    }

    public Node(String id, int move, Node parentNode, Player activePlayer, Player opponentPlayer, Card hit) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedHit = hit;
    }

//    public Node(Node other) {
//        this.id = other.id;
//        this.move = other.move;
//        this.parentNode = new Node(other.parentNode);
//        this.activePlayer = other.activePlayer.deepCopy();
//        this.opponentPlayer = other.opponentPlayer.deepCopy();
//
//        this.childrenExplored = new ArrayList<>();
//        for (Node child : other.childrenExplored)
//            this.childrenExplored.add(new Node(child));
//
//        this.childrenUnexplored = new ArrayList<>();
//        for (Node child : other.childrenUnexplored)
//            this.childrenUnexplored.add(new Node(child));
//
//        this.wonPlayouts = other.wonPlayouts;
//        this.playedPlayouts = other.playedPlayouts;
//
//        this.performedHit = other.performedHit;
//        this.performedAttack = other.performedAttack;
//        this.performedCards = other.performedCards;
//
//    }


    public String getId() {
        return id;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public List<Node> getChildrenExplored() {
        return childrenExplored;
    }

    public List<Node> getChildrenUnexplored() {
        return childrenUnexplored;
    }

    public void setChildrenUnexplored(List<Node> childrenUnexplored) {
        this.childrenUnexplored = childrenUnexplored;
    }

    public int getWonPlayouts() {
        return wonPlayouts;
    }

    public int getPlayedPlayouts() {
        return playedPlayouts;
    }

    public Attack getPerformedAttack() {
        return performedAttack;
    }

    public List<Card> getPerformedCards() {
        return performedCards;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public void setWonPlayouts(int wonPlayouts) {
        this.wonPlayouts = wonPlayouts;
    }

    public void setPlayedPlayouts(int playedPlayouts) {
        this.playedPlayouts = playedPlayouts;
    }

    public Card getPerformedHit() {
        return performedHit;
    }

    public int getMove() {
        return move;
    }

    public void setChildrenExplored(List<Node> childrenExplored) {
        this.childrenExplored = childrenExplored;
    }

    //    public List<Card> getActivePlayersInactiveWarriors() {
//        return activePlayersInactiveWarriors;
//    }

    @Override
    public String toString() {
        return "Node " + id + " (score " + wonPlayouts + "/" + playedPlayouts + ") - " + activePlayer.getName();
    }

    public List<Node> findAllChildrenNodes() {
        // Get all ready to attack warriors
        List<Card> activePlayersWarriors = new ArrayList<>();
        if (!activePlayer.getWarriors().isEmpty()) {
            for (Card warrior : activePlayer.getWarriors())
                if (warrior.isBeforeAttack())
                    activePlayersWarriors.add(warrior);
        }

        if (!activePlayersWarriors.isEmpty() && performedCards == null) {  // perform Attack
           List<Node> childrenPerformingAttack = new ArrayList<>();
           int idCounter = 0;
           for (int i=0; i<activePlayersWarriors.size(); i++) {
               activePlayersWarriors.get(i).setBeforeAttack(false);
               Card attacker = new Card(activePlayersWarriors.get(i));
               for (int j=0; j<(opponentPlayer.getWarriors().size() + 1); j++) {
                   if (j == 0) { // PlayerAttack
                       Attack attack = new PlayerAttack(attacker, opponentPlayer);
                       Player copyOfOpponentPlayer = opponentPlayer.deepCopy();
                       copyOfOpponentPlayer.setHp(copyOfOpponentPlayer.getHp() - attack.getAttacker().getAttack());
                       childrenPerformingAttack.add(new Node(id + "." + idCounter, move, this,
                               activePlayer.deepCopy(), copyOfOpponentPlayer, attack));
                   }
                   else {
                       Attack attack = new WarriorAttack(attacker, opponentPlayer.getWarriors().get(j - 1));
                       Player copyOfActivePlayer = activePlayer.deepCopy();
                       Player copyOfOpponentPlayer = opponentPlayer.deepCopy();
                       copyOfActivePlayer.performSingleAttack(copyOfOpponentPlayer, attack);
                       childrenPerformingAttack.add(new Node(id + "." + idCounter, move, this,
                               activePlayer.deepCopy(), copyOfOpponentPlayer, attack));
                   }
                   idCounter++;
               }
           }
           return childrenPerformingAttack;
        }
        else if (!activePlayer.getPossibleCardsToPlay().isEmpty() && performedCards == null) {  // play cards
            List<List<Card>> possibleCardsToPlay = activePlayer.getPossibleCardsToPlay();
            List<Node> childrenPerformingCardsPlay = new ArrayList<>();
            for (int i=0; i<possibleCardsToPlay.size(); i++) {
                Player copyOfActivePlayer = activePlayer.deepCopy();
                copyOfActivePlayer.playCards(possibleCardsToPlay.get(i), false);
                childrenPerformingCardsPlay.add(new Node(id + "." + i, move, this, copyOfActivePlayer,
                        opponentPlayer.deepCopy(), possibleCardsToPlay.get(i)));
            }
            return childrenPerformingCardsPlay;
        }

        else { // no more moves to make; change player and hit --> 'Nondeterministic Node'
            int newActivePlayersMove = move + 1;

            for (Card warrior : opponentPlayer.getWarriors())
                warrior.setBeforeAttack(true);

            List<Card> newActivePlayersDeck = opponentPlayer.deepCopy().getDeck();
            List<Node> nondeterministicChildren = new ArrayList<>();
            for (int i=0; i<newActivePlayersDeck.size(); i++) {
                Player newActivePlayer = opponentPlayer.deepCopy();
                Player newOpponentPlayer = activePlayer.deepCopy();
                newActivePlayer.updateMana(newActivePlayersMove);

                Card cardFromDeck = newActivePlayersDeck.get(i);
                newActivePlayer.getHand().add(cardFromDeck);
                nondeterministicChildren.add(new Node(id + "." + i, newActivePlayersMove, this,
                        newActivePlayer, newOpponentPlayer, cardFromDeck));
            }

            if (nondeterministicChildren.isEmpty()) {
                Player newActivePlayer = opponentPlayer.deepCopy();
                Player newOpponentPlayer = activePlayer.deepCopy();
                newActivePlayer.updateMana(newActivePlayersMove);

                int emptyDeckPunishment = newActivePlayer.getPunishment();
                newActivePlayer.setPunishment(++emptyDeckPunishment);
                newActivePlayer.setHp(newActivePlayer.getHp() - emptyDeckPunishment);
                nondeterministicChildren.add(new Node(id + ".0", newActivePlayersMove, this,
                        newActivePlayer, newOpponentPlayer, new Card("Empty deck punishment", 0, 0, 0)));
            }
            return nondeterministicChildren;
        }
    }


}
