package mcts;

import attacks.Attack;
import attacks.PlayerAttack;
import attacks.WarriorAttack;
import game.Card;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class Node implements INode {

    public static int playouts = 10;

    private String id;
    private INode parentNode;
    private List<INode> childrenExplored;
    private List<INode> childrenUnexplored;
    private int wonPlayouts;
    private int playedPlayouts;
    private Player activePlayer;
    private Player opponentPlayer;
    private Attack performedAttack;  // attack from parent which result in current node
    private List<Card> playedCards;  // list of played cards (1 or 2) by parent which result in current node


    public Node(String id, INode parentNode, Player activePlayer, Player opponentPlayer) {
        this.id = id;
        this.parentNode = parentNode;
        this.activePlayer = activePlayer;
        this.opponentPlayer = opponentPlayer;

        this.childrenExplored = new ArrayList<>();
        this.childrenUnexplored = findAllChildrenNodes();
        this.wonPlayouts = 0;
        this.playedPlayouts = 0;

        this.performedAttack = null;
        this.playedCards = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public INode getParentNode() {
        return parentNode;
    }

    public Node(String id, INode parentNode, Player activePlayer, Player opponentPlayer, Attack performedAttack) {
        this(id, parentNode, activePlayer, opponentPlayer);
        this.performedAttack = performedAttack;
    }

    public Node(String id, INode parentNode, Player activePlayer, Player opponentPlayer, List<Card> playedCards) {
        this(id, parentNode, activePlayer, opponentPlayer);
        this.playedCards = playedCards;
    }

    private List<INode> findAllChildrenNodes() {
        if (!activePlayer.getWarriors().isEmpty()) {  // perform Attack
           List<INode> childrenPerformingAttack = new ArrayList<>();
           int idCounter = 0;
           for (int i=0; i<activePlayer.getWarriors().size(); i++) {
               for (int j=0; j<(opponentPlayer.getWarriors().size() + 1); j++) {
                   if (j == 0) { // PlayerAttack
                       Attack attack = new PlayerAttack(activePlayer.getWarriors().get(i), opponentPlayer);
                       Player copyOfOpponentPlayer = opponentPlayer.deepCopy();
                       copyOfOpponentPlayer.setHp(copyOfOpponentPlayer.getHp() - attack.getAttacker().getAttack());
                       childrenPerformingAttack.add(new Node(id + "." + idCounter, this, activePlayer.deepCopy(),
                               copyOfOpponentPlayer, attack));
                   }
                   else {
                       Attack attack = new WarriorAttack(activePlayer.getWarriors().get(i), opponentPlayer.getWarriors().get(j - 1));
                       Player copyOfActivePlayer = activePlayer.deepCopy();
                       Player copyOfOpponentPlayer = opponentPlayer.deepCopy();
                       copyOfActivePlayer.performSingleAttack(copyOfOpponentPlayer, attack);
                       childrenPerformingAttack.add(new Node(id + "." + idCounter, this, activePlayer.deepCopy(),
                               copyOfOpponentPlayer, attack));
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
                childrenPerformingCardsPlay.add(new Node(id + "." + i, this, copyOfActivePlayer,
                        opponentPlayer.deepCopy(), possibleCardsToPlay.get(i)));
            }
            return childrenPerformingCardsPlay;
        }
        else { // no more moves to make; change player and hit --> Nondeterministic Node
            List<Card> activePlayersDeck = activePlayer.getDeck();
            List<INode> nondeterministicChildren = new ArrayList<>();
            for (int i=0; i<activePlayersDeck.size(); i++) {
                nondeterministicChildren.add((INode) new NondeterministicNode(id + "." + i, this,
                        new Card(activePlayersDeck.get(i)), opponentPlayer.getName()));
            }
            return nondeterministicChildren;
        }
    }


}
