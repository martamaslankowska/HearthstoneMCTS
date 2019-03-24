package game;

import mcts.Node;
import players.MCTSPlayer;
import players.Player;
import players.RandomPlayer;

import java.util.*;

import static game.Main.CARDS;

public class Tests {

    public static void main(String args[]) {
        int wins = 0;
        int plays = 40;

        for (int i=0; i<plays; i++) {
            Player firstPlayer = new RandomPlayer("Random player");
            Player secondPlayer = new MCTSPlayer("MCTS player");

            Game game = new Game(firstPlayer, secondPlayer);
            Player winner = game.gamePlay(false);
            if (winner == secondPlayer)
                wins += 1;
            System.out.println(winner + " wins the game");
        }

        System.out.println("\n\nMCTS won " + wins + "/" + plays + " times");

    }

//    public static void testMCTSFindingChildNodes(int move, Player activePlayer, Player opponentPlayer) {
//        Node node = new Node(move, activePlayer, opponentPlayer);
//        node.setChildrenUnexplored(node.findAllChildrenNodes());
//
//        List<Node> children = node.getChildrenUnexplored();
//        for (Node child : children)
//            System.out.println(child);
//
//        List<Node> childrenOfChildren = ((Node)children.get(0)).findAllChildrenNodes();
//        ((Node)children.get(0)).setChildrenUnexplored(childrenOfChildren);
//        System.out.println("\nFirst child children:");
//        for (Node child : childrenOfChildren)
//            System.out.println(child);
//
////        ((Node)children.get(1)).setChildrenUnexplored(((Node)children.get(1)).findAllChildrenNodes());
////        System.out.println("\nSecond child children:");
////        for (Node child : ((Node)children.get(1)).getChildrenUnexplored())
////            System.out.println(child);
//
//    }


}
