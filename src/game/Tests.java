package game;

import mcts.Node;
import players.AggressivePlayer;
import players.MCTSPlayer;
import players.Player;
import players.RandomPlayer;

import java.util.*;

import static game.Main.CARDS;

public class Tests {

    public static void main(String args[]) {
        int wins = 0;
        int plays = 25;
        Player testedPlayer = null;

        for (int i=0; i<plays; i++) {
            Player randomPlayer = new RandomPlayer("Random player");
            Player MCTSPlayer = new MCTSPlayer("MCTS player");
            Player aggressivePlayer = new AggressivePlayer("Aggressive player");
            Player aggressiveOpponent = new AggressivePlayer("Aggressive opponent");

            testedPlayer = aggressiveOpponent;

            Game game = new Game(aggressiveOpponent, aggressivePlayer);
            Player winner = game.gamePlay(false);
            
            if (winner == testedPlayer)
                wins += 1;
            System.out.println((i+1) + ". " + winner + " wins the game");
        }

        System.out.println("\n\n" + testedPlayer.getName() + " won " + wins + "/" + plays + " times");

    }

    /*
    * STATISTICS
    *
    * Random vs. MCTS - 60%
    * MCTS vs. RANDOM - 80%
    *
    * MCTS vs. Aggressive - 35-40% (from 500 to 2000 iterations & 100 playouts)
    * MCTS vs. Aggressive - 75-80% (2000 iterations & 100 playouts; having cards with smaller mana)
    * Aggressive vs. Aggressive - 80-90% (cards with smaller mana)
    *
    * */

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
