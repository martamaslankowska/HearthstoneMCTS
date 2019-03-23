package mcts;

import game.Card;

import java.util.ArrayList;
import java.util.List;

public class NondeterministicNode implements INode {

    private String id;
    private INode parentNode;
    private List<INode> childrenNodes;
    private Card cardFromDeck;
    private String newActivePlayerName;

    public NondeterministicNode(String id, INode parentNode, Card cardFromDeck, String newActivePlayerName) {
        this.parentNode = parentNode;
        this.cardFromDeck = cardFromDeck;
        this.newActivePlayerName = newActivePlayerName;  // add mana while swapping players

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
}
