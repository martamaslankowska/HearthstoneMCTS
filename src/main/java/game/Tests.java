package game;

import com.github.sh0nk.matplotlib4j.Plot;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;
import players.*;

import java.util.*;

import static game.Main.CARDS;

public class Tests {

    public final static int ITERATIONS_NUMBER=300;
    public final static int PLAYOUTS_NUMBER=100;

    public static void main(String args[]) {

        Player randomPlayer = new RandomPlayer("Random player");
        Player MCTSPlayer = new MCTSPlayer("MCTS player",ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM);
        Player aggressivePlayer = new AggressivePlayer("Aggressive player");
        Player aggressiveOpponent = new AggressivePlayer("Aggressive opponent");
        Player controlling= new ControllingPlayer("Controlling");

        //simpleAccuracyAverageTest(controlling,randomPlayer,controlling,100,10);
        playoutNumberImpactTest();

//        manaImpactTest(aggressivePlayer,randomPlayer,aggressivePlayer,1000,0);
//        manaImpactTest(aggressivePlayer,randomPlayer,aggressivePlayer,1000,1);
//        manaImpactTest(aggressivePlayer,randomPlayer,aggressivePlayer,1000,2);
    }

    public static double simpleAccuracyTest(Player firstPlayer, Player secondPlayer, Player testedPlayer, int playsNumber){
        int wins = 0;
        int plays = playsNumber;

        for (int i = 0; i < plays; i++) {
            Player first=firstPlayer;
            Player second=secondPlayer;
            switch (firstPlayer.getClass().getSimpleName()){
                case "MCTSPlayer":first=new MCTSPlayer(firstPlayer.getName(),ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM);break;
                case "RandomPlayer":first=new RandomPlayer(firstPlayer.getName());break;
                case "ControllingPlayer":{
                    first=new ControllingPlayer(firstPlayer.getName());
                    break;
                }
                case "AggressivePlayer":first=new AggressivePlayer(firstPlayer.getName());break;
                //default:first=new ControllingPlayer(firstPlayer.getName());
            }
            switch (secondPlayer.getClass().getSimpleName()){
                case "MCTSPlayer":second=new MCTSPlayer(secondPlayer.getName(),ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM);break;
                case "RandomPlayer":second=new RandomPlayer(secondPlayer.getName());break;
                case "ControllingPlayer":second=new ControllingPlayer(secondPlayer.getName());break;
                case "AggressivePlayer":second=new AggressivePlayer(secondPlayer.getName());break;
                //default:second=new RandomPlayer(secondPlayer.getName());
            }

            Game game = new Game(first, second);
            Player winner = game.gamePlay(false);

            if (winner.getName() == testedPlayer.getName())
                wins += 1;
            System.out.println((i + 1) + ". " + winner + " wins the game");
        }

        System.out.println("\n\n" + testedPlayer.getName() + " won " + wins + "/" + plays + " times");
        return ((double)wins)/plays;
    }

    public static double simpleAccuracyAverageTest(Player firstPlayer, Player secondPlayer, Player testedPlayer, int playsNumber, int averageTimes){
        double scoreTotal = 0;
        for (int i=0; i<averageTimes; i++) {
            scoreTotal+=simpleAccuracyTest(firstPlayer,secondPlayer,testedPlayer,playsNumber);
        }
        double average=scoreTotal/averageTimes;
        System.out.println("\n\n" + testedPlayer.getName() + " has " + 100*average+ "% accuracy");
        return average;
    }


    public static void manaImpactTest(Player firstPlayer, Player secondPlayer, Player testedPlayer, int playsNumber, int manaDrop) {

        for (int i=0;i< CARDS.size();i++)
        {
            Card card=CARDS.get(i);
            int newManaValue=card.getMana()-manaDrop;
            card.setMana(newManaValue<=0?1:newManaValue);
        }
        simpleAccuracyTest(firstPlayer,secondPlayer,testedPlayer,playsNumber);
    }

    public static void playoutNumberImpactTest(){
        int step=200;
        int min=200;
        int max=1000;
        List<Integer> domain= new ArrayList<>();
        List<Double> results=new ArrayList<>();

        Player randomPlayer = new RandomPlayer("Random player");

        for(int i=0;i*step+min<=max;i++)
        {
            Player MCTSPlayer = new MCTSPlayer("MCTS player",ITERATIONS_NUMBER,min+i*step, MCTSPlayoutHeuristic.RANDOM);
            domain.add(i*step+min);
            results.add(simpleAccuracyTest(MCTSPlayer,randomPlayer,MCTSPlayer,100));
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba playotów");
        plt.ylabel("Accuracy");
        plt.title("Playouts impact test");
        plt.legend();
        try {
            plt.show();
        }catch (Exception ex){}
    }

    public static void playingOrderImpactTest(int numberOfPlays, int averageRepeats){

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
    * Controlling vs. MCTS (400) 37%
    * Controlling vs. MCTS (1000) 42%
    * Spadek many działa korzystnie na agresywnego
    * Spadek many działa niekorzystnie na kontrolującego
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
