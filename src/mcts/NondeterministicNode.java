package mcts;

import game.Card;
import players.Player;

import java.util.ArrayList;
import java.util.List;

public class NondeterministicNode implements INode {

    private String id;
    private INode parentNode;
    private List<INode> childrenNodes;
    private Card cardFromDeck;
    private Player newActivePlayer;

    public NondeterministicNode(String id, INode parentNode, Card cardFromDeck, Player newActivePlayer) {
        this.id = id;
        this.parentNode = parentNode;
        this.cardFromDeck = cardFromDeck;
        this.newActivePlayer = newActivePlayer;  // add mana while swapping players

        this.childrenNodes = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public INode getParentNode() {
        return parentNode;
    }


    @Override
    public String toString() {
        return "NondeterNode " + id + " - hit of " + cardFromDeck + " for " + newActivePlayer.getName();
    }

    public void swapPlayers() {
        System.out.println();
    }

}
