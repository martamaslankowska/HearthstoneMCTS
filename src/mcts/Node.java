package mcts;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Node implements INode {

    private String id;
    private int move;
    private INode parentNode;
    private List<INode> childrenExplored;
    private List<INode> childrenUnexplored;
    private int wonPlayouts;
    private int playedPlayouts;
    private Player activePlayer;
    private Player opponentPlayer;
    private List<Card> activePlayersInactiveWarriors;  // warriors that already attacked somebody in current move
    private Card performedHit;  // hit which result in current node ~ parent: NondeterministicNode
    private Attack performedAttack;  // attack from parent which result in current node
    private List<Card> performedCards;  // list of played cards (1 or 2) by parent which result in current node


    public Node(String id, int move, INode parentNode, Player activePlayer, Player opponentPlayer) {
        this.id = id;
        this.move = move;
        this.parentNode = parentNode;
        this.activePlayer = activePlayer;
        this.opponentPlayer = opponentPlayer;

        this.childrenExplored = new ArrayList<>();
        this.childrenUnexplored = new ArrayList<>();
        this.wonPlayouts = 0;
        this.playedPlayouts = 0;

        this.activePlayersInactiveWarriors = new ArrayList<>();
        this.performedHit = null;
        this.performedAttack = null;
        this.performedCards = null;
    }

    public Node(Player activePlayer) {
        this.activePlayer = activePlayer;
        this.id = "0";
    }

    public Node(int move, Player activePlayer, Player opponentPlayer) {
        this("0", move, (INode) null, activePlayer, opponentPlayer);
    }

    public Node(String id, int move, INode parentNode, Player activePlayer, Player opponentPlayer, Attack performedAttack) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedAttack = performedAttack;
    }

    public Node(String id, int move, INode parentNode, Player activePlayer, Player opponentPlayer, Attack performedAttack, List<Card> inactiveWarriors) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedAttack = performedAttack;
        this.activePlayersInactiveWarriors = inactiveWarriors;
    }

    public Node(String id, int move, INode parentNode, Player activePlayer, Player opponentPlayer, List<Card> performedCards) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedCards = performedCards;
    }

    public Node(String id, int move, INode parentNode, Player activePlayer, Player opponentPlayer, Card hit) {
        this(id, move, parentNode, activePlayer, opponentPlayer);
        this.performedHit = hit;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public INode getParentNode() {
        return parentNode;
    }

    public List<INode> getChildrenExplored() {
        return childrenExplored;
    }

    public List<INode> getChildrenUnexplored() {
        return childrenUnexplored;
    }

    public void setChildrenUnexplored(List<INode> childrenUnexplored) {
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

    public List<Card> getActivePlayersInactiveWarriors() {
        return activePlayersInactiveWarriors;
    }

    @Override
    public String toString() {
        return "Node " + id + " (score " + wonPlayouts + "/" + playedPlayouts + ") - " + activePlayer.getName();
    }

    public List<INode> findAllChildrenNodes() {
        if (!activePlayer.getWarriors().isEmpty() && performedCards == null) {  // perform Attack
           List<INode> childrenPerformingAttack = new ArrayList<>();
           int idCounter = 0;
           for (int i=0; i<activePlayer.getWarriors().size(); i++) {
               for (int j=0; j<(opponentPlayer.getWarriors().size() + 1); j++) {
                   Card attacker = activePlayer.getWarriors().get(i);
                   activePlayersInactiveWarriors.add(attacker);
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
        else if (!activePlayer.getPossibleCardsToPlay().isEmpty()) {  // play cards
            List<List<Card>> possibleCardsToPlay = activePlayer.getPossibleCardsToPlay();
            List<INode> childrenPerformingCardsPlay = new ArrayList<>();
            for (int i=0; i<possibleCardsToPlay.size(); i++) {
                Player copyOfActivePlayer = activePlayer.deepCopy();
                copyOfActivePlayer.playCards(possibleCardsToPlay.get(i), false);
                childrenPerformingCardsPlay.add(new Node(id + "." + i, move, this, copyOfActivePlayer,
                        opponentPlayer.deepCopy(), possibleCardsToPlay.get(i)));
            }
            return childrenPerformingCardsPlay;
        }
        // TODO add punishments if deck is empty and get card from the deck if it's not
        else { // no more moves to make; change player and hit --> 'Nondeterministic Node'
            Player newActivePlayer = opponentPlayer.deepCopy();
            Player newOpponentPlayer = activePlayer.deepCopy();
            int newActivePlayersMove = move + 1;
            newActivePlayer.updateMana(newActivePlayersMove);

            List<Card> newActivePlayersDeck = newActivePlayer.getDeck();
            List<INode> nondeterministicChildren = new ArrayList<>();
            for (int i=0; i<newActivePlayersDeck.size(); i++) {
                nondeterministicChildren.add(new Node(id + "." + i, newActivePlayersMove, this,
                        newActivePlayer, newOpponentPlayer, newActivePlayersDeck.get(i)));
            }

            if (nondeterministicChildren.isEmpty()) {
                newActivePlayer.hit();
            }
            return nondeterministicChildren;
        }
    }


}
