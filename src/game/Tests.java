package game;

import mcts.Node;
import players.Player;
import players.RandomPlayer;

import java.util.*;

import static game.Main.CARDS;

public class Tests {

    public static void main(String args[]) {

        Player activePlayer = new RandomPlayer("MCTS player");
        Player inactivePlayer = new RandomPlayer("Opponent player");

        List<Card> listOfActivePlayersWarriors = new ArrayList<>();
        listOfActivePlayersWarriors.add(CARDS.get(0));
        listOfActivePlayersWarriors.add(CARDS.get(1));
        listOfActivePlayersWarriors.add(CARDS.get(2));

        List<Card> listOfInactivePlayersWarriors = new ArrayList<>();
        listOfInactivePlayersWarriors.add(CARDS.get(4));
        listOfInactivePlayersWarriors.add(CARDS.get(5));

        List<Card> listOfCardsInHand = new ArrayList<>();
        listOfCardsInHand.add(CARDS.get(0));
        listOfCardsInHand.add(CARDS.get(2));
        listOfCardsInHand.add(CARDS.get(4));
        listOfCardsInHand.add(CARDS.get(6));


        activePlayer.setMana(4);
        inactivePlayer.setMana(3);

        activePlayer.getDeck().addAll(CARDS);
        inactivePlayer.getDeck().addAll(CARDS);

        activePlayer.setHand(listOfCardsInHand);
        inactivePlayer.setHand(listOfCardsInHand);

//        activePlayer.setWarriors(listOfActivePlayersWarriors);
//        inactivePlayer.setWarriors(listOfInactivePlayersWarriors);


        int move = 7;
        testMCTSFindingChildNodes(move, activePlayer, inactivePlayer);

    }

    public static void testMCTSFindingChildNodes(int move, Player activePlayer, Player opponentPlayer) {
        Node node = new Node(move, activePlayer, opponentPlayer);
        node.setChildrenUnexplored(node.findAllChildrenNodes());

        List<Node> children = node.getChildrenUnexplored();
        for (Node child : children)
            System.out.println(child);

        List<Node> childrenOfChildren = ((Node)children.get(0)).findAllChildrenNodes();
        ((Node)children.get(0)).setChildrenUnexplored(childrenOfChildren);
        System.out.println("\nFirst child children:");
        for (Node child : childrenOfChildren)
            System.out.println(child);

//        ((Node)children.get(1)).setChildrenUnexplored(((Node)children.get(1)).findAllChildrenNodes());
//        System.out.println("\nSecond child children:");
//        for (Node child : ((Node)children.get(1)).getChildrenUnexplored())
//            System.out.println(child);

    }

}
