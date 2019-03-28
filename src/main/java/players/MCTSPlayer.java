package players;

import attacks.Attack;
import game.Card;
import mcts.MCTS;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MCTSPlayer extends Player {

    private Node currentRootNode;
    private MCTS tree;
    private int MCTSIterations;
    private int playputsCount;
    private MCTSPlayoutHeuristic playoutHeuristic;

    private MCTSPlayer() {}

    public MCTSPlayer(String name, int iterations, int playouts, MCTSPlayoutHeuristic playoutHeuristic) {
        super(name);
        this.currentRootNode = new Node(this);
        this.MCTSIterations = iterations;
        this.playputsCount=playouts;
        this.playoutHeuristic=playoutHeuristic;
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
            if (bestChildNode.getPerformedAttack() != null) {
                Attack attack = bestChildNode.getPerformedAttack();
                possibleAttacks.add(attack);

//                System.out.println("\n    MCTS ATTACK:");
//                int best = 0;
//                for (int i=0; i<currentRootNode.getChildrenExplored().size(); i++) {
//                    Node child = currentRootNode.getChildrenExplored().get(i);
//                    double stats = (double) child.getWonPlayouts() / child.getPlayedPlayouts();
//                    System.out.println("    --> " + (i+1) + ". " + child.getPerformedAttack() + " | " + child.getWonPlayouts() + "/" + child.getPlayedPlayouts() + " = " + stats);
//                    if (bestChildNode == child)
//                        best = i;
//                }
//                System.out.println("    BEST: " + (best+1) + ". " + bestChildNode.getPerformedAttack());

            }
            currentRootNode = bestChildNode;
        }
        return Arrays.asList(possibleAttacks);
    }

    @Override
    public List<List<Card>> getPossibleCardsToPlay(Player opponent, int move) {
        if (currentRootNode.getPerformedHit() == null) {

//            System.out.println("\n    MCTS CARDS TO PLAY:");
//            int best = 0;
//            for (int i=0; i<currentRootNode.getParentNode().getChildrenExplored().size(); i++) {
//                Node child = currentRootNode.getParentNode().getChildrenExplored().get(i);
//                double stats = (double) child.getWonPlayouts() / child.getPlayedPlayouts();
//                System.out.println("    --> " + (i+1) + ". " + child.getPerformedCards() + " | " + child.getWonPlayouts() + "/" + child.getPlayedPlayouts() + " = " + stats);
//                if (currentRootNode == child)
//                    best = i;
//            }
//            System.out.println("    BEST: " + (best+1) + ". " + currentRootNode.getPerformedCards() + "\n");


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
