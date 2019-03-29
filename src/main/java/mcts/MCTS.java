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

    public static final double C = 5;
    public int PLAYOUTS_NO = 100;
    public String MCTSPlayerName = "";
    public MCTSPlayoutHeuristic playoutHeuristic=MCTSPlayoutHeuristic.RANDOM;

    private Node rootNode;

    public MCTS(String activePlayerName, int move, Node rootNode,int playputs,MCTSPlayoutHeuristic playoutHeuristic) {
        this.rootNode = rootNode;
        this.MCTSPlayerName = activePlayerName;
        this.PLAYOUTS_NO=playputs;
        this.playoutHeuristic=playoutHeuristic;
    }


    public Node mcts(int iterations, boolean verbose) {
        Node deepestNode = rootNode;
        int level = 0;

        for (int i=0; i<iterations; i++) {
            if (verbose)
                System.out.println("    // iteration " + (i+1));

            // Selection
            Node nodeToExpand = selectChild();

            // Expansion
            Node node = expandUnexploredChild(nodeToExpand);

            // Playouts
            int wins = 0;
            for (int j = 0; j<PLAYOUTS_NO; j++) {
                Player winner = playoutSimulation(node);
                if (winner.getName().equals(MCTSPlayerName))
                    wins++;
            }
            updateNodeScore(node, wins);
            if (verbose)
                System.out.println(node);

//            // TMP
//            int l = 0;
//            Node tmp = node;
//            while(!tmp.getId().equals(rootNode.getId())) {
//                l++;
//                tmp = tmp.getParentNode();
//            }
//            if (l > level) {
//                deepestNode = node;
//                l = level;
//            }

            // Backpropagation

            backpropagateResults(node, wins);
        }
        if (verbose)
            System.out.println("    ...finished " + iterations + " iterations");
        Node bestRootNodeChild = pickBestRootNodeChild();


//        System.out.println("\nDeepest node:");
//        while(deepestNode != null) {
//            System.out.println(deepestNode);
//            deepestNode = deepestNode.getParentNode();
//        }
//        System.out.println("Best node:\n" + bestRootNodeChild + "\n");


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

    private Player playoutSimulation(Node node){
        switch (playoutHeuristic){
            case RANDOM: return randomPlayoutSimulation(node);
            case HEURISTIC_1:return playoutHeuristic1(node);
            case HEURISTIC_2:return playoutHeuristic2(node);
            default: return randomPlayoutSimulation(node);
        }
    }


    private Player randomPlayoutSimulation(Node node) {
        RandomPlayer activePlayer = new RandomPlayer(node.getActivePlayer());
        RandomPlayer inactivePlayer = new RandomPlayer(node.getOpponentPlayer());
        int move = node.getMove();

        // Find all warriors which are able to attack in this round
        List<Card> warriorsBeforeAttack = new ArrayList<>();
        if (node.getPerformedCards() == null) {  // either while attacking or after hit
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
        }

        // Change active player (swap players) - after cards playing
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



    //TODO implement
    private Player playoutHeuristic1(Node node) {
        RandomPlayer activePlayer = new RandomPlayer(node.getActivePlayer());
        return activePlayer;
    }

    //TODO implement
    private Player playoutHeuristic2(Node node) {
        RandomPlayer activePlayer = new RandomPlayer(node.getActivePlayer());
        return activePlayer;
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
        if (!nodeToUpdate.getActivePlayer().getName().equals(MCTSPlayerName))
            wins = -wins;
        nodeToUpdate.setWonPlayouts(nodeToUpdate.getWonPlayouts() + wins);
        nodeToUpdate.setPlayedPlayouts(nodeToUpdate.getPlayedPlayouts() + PLAYOUTS_NO);
    }

    private void backpropagateResults(Node nodeWhichMadePlayouts, int wins) {
        Node parentNode = nodeWhichMadePlayouts.getParentNode();
        while (parentNode != null) {
            updateNodeScore(parentNode, wins);
            parentNode = parentNode.getParentNode();
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
