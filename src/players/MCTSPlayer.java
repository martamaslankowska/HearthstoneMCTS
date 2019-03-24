package players;

import attacks.Attack;
import game.Card;
import mcts.MCTS;
import mcts.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCTSPlayer extends Player {

    private Node currentRootNode;
    private MCTS tree;
    private int MCTSIterations;

    private MCTSPlayer() {}

    public MCTSPlayer(String name) {
        super(name);
        this.currentRootNode = new Node(this);
        this.MCTSIterations = 100;
    }

    @Override
    public Player deepCopy() {
        Player res = new MCTSPlayer();
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
        for (Card warrior : warriors)
            warrior.setBeforeAttack(true);

        List<Attack> possibleAttacks = new ArrayList<>();
        currentRootNode = new Node("0", move, null, this, opponent);
        while (currentRootNode.getPerformedCards() == null && currentRootNode.getPerformedHit() == null) {
            tree = new MCTS(this.getName(), move, currentRootNode);
            Node bestChildNode = tree.mcts(MCTSIterations, false);
            if (bestChildNode.getPerformedAttack() != null) {
                Attack attack = bestChildNode.getPerformedAttack();
                possibleAttacks.add(attack);
            }
            currentRootNode = bestChildNode;
        }
        return Arrays.asList(possibleAttacks);
    }

    @Override
    public List<List<Card>> getPossibleCardsToPlay(Player opponent, int move) {
        if (currentRootNode.getPerformedHit() == null) {
//            tree = new MCTS(this.getName(), move, currentRootNode);
//            Node bestChildNode = tree.mcts(MCTSIterations, false);
            return Arrays.asList(currentRootNode.getPerformedCards());
        }
        else
            return Arrays.asList(new ArrayList<Card>());
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        return possibleCardsToPlay.get(0);
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        return possibleAttacks.get(0);
    }


}
