package mcts;

import attacks.Attack;
import game.Card;
import players.Player;
import players.RandomPlayer;

import static game.Main.random;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MCTS{

    public static final double C = 0.7;
    public final int PLAYOUTS_NO = 10;
    public String MCTSPlayerName = "";

    private Node rootNode;

    public MCTS(String activePlayerName, int move, Node rootNode) {
        this.rootNode = rootNode;
        this.MCTSPlayerName = activePlayerName;
    }


    public Node mcts(int iterations, boolean verbose) {
        for (int i=0; i<iterations; i++) {
//            System.out.println("    // iteration " + i);

            // Selection
            Node nodeToExpand = selectChild();

            // Expansion
            Node node = expandUnexploredChild(nodeToExpand);

            // Playouts
            int wins = 0;
            for (int j = 0; j<PLAYOUTS_NO; j++) {
                Player winner = randomPlayoutSimulation(node);
                if (winner.getName().equals(node.getActivePlayer().getName()))
                    wins++;
            }
            if (!node.getActivePlayer().getName().equals(MCTSPlayerName))
                wins = -wins;

            updateNodeScore(node, wins);

            // Backpropagation
            backpropagateResults(node, wins);
        }
        System.out.println("    ...finished " + iterations + " iterations");
        Node bestRootNodeChild = pickBestRootNodeChild();
        return bestRootNodeChild;
    }


    private Node selectChild() {
        Node nodeToExpand = rootNode;

        while (!nodeToExpand.getChildrenExplored().isEmpty() && nodeToExpand.getChildrenUnexplored().isEmpty()) { // while node doesn't have unvisited children
            nodeToExpand = selectBestChild(nodeToExpand);
            if (nodeToExpand.getPerformedCards() != null) { // NondeterministicNode - pick random child and go further
                if (nodeToExpand.getChildrenExplored().isEmpty() && nodeToExpand.getChildrenUnexplored().isEmpty())
                    nodeToExpand.setChildrenExplored(nodeToExpand.findAllChildrenNodes());
                nodeToExpand = nodeToExpand.getChildrenExplored().get(random.nextInt(nodeToExpand.getChildrenExplored().size()));
            }
        }
        return nodeToExpand;
    }

    private Node selectBestChild(Node parent) {
        List<Node> visitedChildren = parent.getChildrenExplored();
        return visitedChildren.stream()
                .max(Comparator.comparing(child -> calculateUCT(parent, child)))
                .orElseThrow(() -> new AssertionError("visitedChildren cannot be empty"));
    }

    private static double calculateUCT(Node parent, Node child) {
        return (double) child.getWonPlayouts() / child.getPlayedPlayouts() + C * sqrt(2 * log(parent.getPlayedPlayouts()) / child.getPlayedPlayouts());
    }

    private Node expandUnexploredChild(Node nodeToExpand) {
        if (nodeToExpand.getChildrenExplored().isEmpty() && nodeToExpand.getChildrenUnexplored().isEmpty())
            nodeToExpand.setChildrenUnexplored(nodeToExpand.findAllChildrenNodes());
        Node selectedChildToExplore = nodeToExpand.getChildrenUnexplored().get(0);
        nodeToExpand.getChildrenExplored().add(selectedChildToExplore);
        nodeToExpand.getChildrenUnexplored().remove(selectedChildToExplore);
        return selectedChildToExplore; // selected childrenNode to expand
    }

    private Player randomPlayoutSimulation(Node node) {
        RandomPlayer activePlayer = new RandomPlayer(node.getActivePlayer());
        RandomPlayer inactivePlayer = new RandomPlayer(node.getOpponentPlayer());
        int move = node.getMove();

        // Find all warriors which are able to attack in this round
        List<Card> warriorsBeforeAttack = new ArrayList<>();
        for (Card warrior : activePlayer.getWarriors()) {
            if (warrior.isBeforeAttack())
                warriorsBeforeAttack.add(warrior);
        }

        // Attack
        if (!warriorsBeforeAttack.isEmpty()) {
            List<List<Attack>> possibleAttacks = activePlayer.getPossibleAttacks(inactivePlayer, move);
            activePlayer.attackOpponentsCards(inactivePlayer, activePlayer.selectAttacksToPlay(inactivePlayer, possibleAttacks), false);
        }
        // Play cards
        List<List<Card>> cardsToPlay = activePlayer.getPossibleCardsToPlay();
        activePlayer.playCards(activePlayer.selectCardsToPlay(cardsToPlay), false);

        // Change active player - swap players
        RandomPlayer tmpPlayer = activePlayer;
        activePlayer = inactivePlayer;
        inactivePlayer = tmpPlayer;


        while(getWinner(activePlayer, inactivePlayer) == null) {
            move++;
            activePlayer.updateMana(move);
            activePlayer.hit();
            if (getWinner(activePlayer, inactivePlayer) != null)
                return getWinner(activePlayer, inactivePlayer);

            List<Attack> selectedAttacks = activePlayer.selectAttacksToPlay(inactivePlayer, activePlayer.getPossibleAttacks(inactivePlayer, move));
            List<Card> selectedCardsToPlay = activePlayer.selectCardsToPlay(activePlayer.getPossibleCardsToPlay(inactivePlayer, move));
            activePlayer.attackOpponentsCards(inactivePlayer, selectedAttacks, false);
            activePlayer.playCards(selectedCardsToPlay, false);

            // Change active player - swap players
            RandomPlayer deactivatedPlayer = activePlayer;
            activePlayer = inactivePlayer;
            inactivePlayer = deactivatedPlayer;
        }

        return getWinner(activePlayer, inactivePlayer);
    }

    private Player getWinner(RandomPlayer activePlayer, RandomPlayer inactivePlayer) {
        if (activePlayer.getHp() <= 0)
            return inactivePlayer;
        if (inactivePlayer.getHp() <= 0)
            return activePlayer;
        else
            return null;
    }



    private void updateNodeScore(Node nodeToUpdate, int wins) {
        nodeToUpdate.setWonPlayouts(nodeToUpdate.getWonPlayouts() + wins);
        nodeToUpdate.setPlayedPlayouts(nodeToUpdate.getPlayedPlayouts() + PLAYOUTS_NO);
    }

    private void backpropagateResults(Node nodeWhichMadePlayouts, int wins) {
        Node parentNode = (Node) nodeWhichMadePlayouts.getParentNode();
        while (parentNode != null) {
            updateNodeScore(parentNode, wins);
            parentNode = (Node) parentNode.getParentNode();
        }
    }

    private Node pickBestRootNodeChild() {
        Node bestChild = (Node) rootNode.getChildrenExplored().get(0);
        float bestPercentageOfWins = ((float) bestChild.getWonPlayouts()) / ((float) bestChild.getPlayedPlayouts());
        for (int i=1; i<rootNode.getChildrenExplored().size(); i++) {
            Node child = (Node) rootNode.getChildrenExplored().get(i);
            float percentageOfWins = ((float) child.getWonPlayouts()) / ((float) child.getPlayedPlayouts());
            if (percentageOfWins > bestPercentageOfWins) {
                bestChild = child;
                bestPercentageOfWins = percentageOfWins;
            }
        }
        return bestChild;
    }


}
