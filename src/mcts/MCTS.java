package mcts;

import attacks.Attack;
import game.Card;
import players.Player;
import players.RandomPlayer;

import static game.Main.random;

import java.util.List;

public class MCTS{

    public final int PLAYOUTS_NO = 10;
    public String MCTSPlayerName = "";

    private Node rootNode;

    public MCTS(String activePlayerName, int move, Node rootNode) {
        this.rootNode = rootNode;
        this.MCTSPlayerName = activePlayerName;
    }


    public Node mcts(int iterations, boolean verbose) {
        for (int i=0; i<iterations; i++) {
            // Selection
            Node nodeToExpand = selectChild();

            // Expansion
            Node node = expandUnexploredChild(nodeToExpand);

            // Playouts
            int wins = 0;
            for (int j = 0; j<PLAYOUTS_NO; j++) {
                Player winner = randomPlayoutSimulation(node);
                if (winner.getName() == node.getActivePlayer().getName())
                    wins++;
            }
            if (node.getActivePlayer().getName() != MCTSPlayerName)
                wins = -wins;

            updateNodeScore(node, wins);

            // Backpropagation
            backpropagateResults(node, wins);
        }
        Node bestRootNodeChild = pickBestRootNodeChild();
        return bestRootNodeChild;
    }


    private Node selectChild() {
        Node nodeToExpand = rootNode;

        while (!nodeToExpand.getChildrenUnexplored().isEmpty()) { // while node doesn't have unvisited children
            nodeToExpand = selectBestNode(nodeToExpand.getChildrenExplored());
            if (nodeToExpand.getPerformedHit() != null) { // NondeterministicNode - pick random and go further
                nodeToExpand = (Node) nodeToExpand.getChildrenUnexplored().get(random.nextInt(nodeToExpand.getChildrenUnexplored().size()));
            }
        }
        return nodeToExpand;
    }

    private Node selectBestNode(List<INode> visitedChildren) {
        return null;
    }

    private Node expandUnexploredChild(Node nodeToExpand) {
        if (nodeToExpand.getChildrenExplored().isEmpty() && nodeToExpand.getChildrenUnexplored().isEmpty())
            nodeToExpand.setChildrenUnexplored(nodeToExpand.findAllChildrenNodes());
        return (Node) nodeToExpand.getChildrenUnexplored().get(0); // select childrenNode to expand
    }

    private Player randomPlayoutSimulation(Node node) {
        RandomPlayer activePlayer = new RandomPlayer(node.getActivePlayer());
        RandomPlayer inactivePlayer = new RandomPlayer(node.getOpponentPlayer());
        int move = node.getMove();

        if (!node.getActivePlayersInactiveWarriors().isEmpty()) {
            if (activePlayer.getWarriors().size() > node.getActivePlayersInactiveWarriors().size()) { // attack
                for (int i=0; i<node.getActivePlayersInactiveWarriors().size(); i++) // remove inactive warriors
                    activePlayer.getWarriors().remove(node.getActivePlayersInactiveWarriors().get(i));
                // attack
                List<List<Attack>> attacks = activePlayer.getPossibleAttacks(inactivePlayer, move);
                activePlayer.attackOpponentsCards(inactivePlayer, attacks.get(0), false);
            }
            // play cards
            List<List<Card>> cardsToPlay = activePlayer.getPossibleCardsToPlay();
            activePlayer.playCards(activePlayer.selectCardsToPlay(cardsToPlay), false);

            // Change active player - swap players
            RandomPlayer deactivatedPlayer = activePlayer;
            activePlayer = inactivePlayer;
            inactivePlayer = deactivatedPlayer;
        }

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
