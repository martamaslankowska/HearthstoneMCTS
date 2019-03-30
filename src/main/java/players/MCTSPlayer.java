package players;

import attacks.Attack;
import game.Card;
import mcts.MCTS;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tests.BFS.treeVisualisationBFS;

public class MCTSPlayer extends Player {

    private Node currentRootNode;
    private MCTS tree;
    private int MCTSIterations;
    private int playputsCount;
    private MCTSPlayoutHeuristic playoutHeuristic;
    private boolean verbose=false;

    private MCTSPlayer() {}

    public MCTSPlayer(String name, int iterations, int playouts, MCTSPlayoutHeuristic playoutHeuristic, boolean verbose) {
        super(name);
        this.currentRootNode = new Node(this);
        this.MCTSIterations = iterations;
        this.playputsCount=playouts;
        this.playoutHeuristic=playoutHeuristic;
        this.verbose=verbose;
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
            tree = new MCTS(this.getName(), move, currentRootNode,playputsCount,playoutHeuristic);
            Node bestChildNode = tree.mcts(MCTSIterations, false);

            // running bfs on whole tree - results are saved to file
            //treeVisualisationBFS(currentRootNode, move);

            if (bestChildNode.getPerformedAttack() != null) {
                Attack attack = bestChildNode.getPerformedAttack();
                possibleAttacks.add(attack);

                // printing statistics for all siblings + best one
                //printSiblingsStatistics(true, bestChildNode);
            }
            currentRootNode = bestChildNode;
        }
        return Arrays.asList(possibleAttacks);
    }

    @Override
    public List<List<Card>> getPossibleCardsToPlay(Player opponent, int move) {
        if (currentRootNode.getPerformedHit() == null) {
            // printing statistics for all siblings + best one
            printSiblingsStatistics(false, null);

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


    private void printSiblingsStatistics(boolean isAttack, Node bestNode) {

        List<Node> children = new ArrayList<>();
        if (isAttack) {
            if(verbose)
                System.out.println("\n    MCTS ATTACKS:");
            children = currentRootNode.getChildrenExplored();
        }
        else {
            if(verbose)
                System.out.println("\n    MCTS CARDS TO PLAY:");
            children = currentRootNode.getParentNode().getChildrenExplored();
        }

        int best = 0;
        for (int i=0; i<children.size(); i++) {
            Node child = children.get(i);
            double stats = (double) child.getWonPlayouts() / child.getPlayedPlayouts();
            String statistic = String.format("%.3f", stats*100) + "%";
            if (isAttack&&verbose)
                System.out.println("    --> " + (i+1) + ". " + child.getPerformedAttack() + " | " + child.getWonPlayouts() + "/" + child.getPlayedPlayouts() + " = " + statistic);
            else if (verbose)
                System.out.println("    --> " + (i+1) + ". " + child.getPerformedCards() + " | " + child.getWonPlayouts() + "/" + child.getPlayedPlayouts() + " = " + statistic);

            if (isAttack) {
                if (bestNode == child)
                    best = i;
            }
            else
                if (currentRootNode == child)
                    best = i;
        }
        if (isAttack&&verbose)
            System.out.println("    BEST: " + (best+1) + ". " + bestNode.getPerformedAttack() + "\n");
        else if (verbose)
            System.out.println("    BEST: " + (best+1) + ". " + currentRootNode.getPerformedCards() + "\n");
    }


}
