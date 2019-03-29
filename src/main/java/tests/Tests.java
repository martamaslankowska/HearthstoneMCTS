package tests;

import com.github.sh0nk.matplotlib4j.Plot;
import game.Card;
import game.Game;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;
import players.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import static game.Main.CARDS;
import static game.Main.random;

public class Tests {

    public final static int ITERATIONS_NUMBER=300;
    public final static int PLAYOUTS_NUMBER=100;

    public static void simpleTests() {

        int wins = 0;
        int iterations = 25;
        Player testedPlayer = null;

        for (int i=0; i<iterations; i++) {
            Player random = new RandomPlayer("Random player");
            Player MCTS = new MCTSPlayer("MCTS player", 1000, 100, MCTSPlayoutHeuristic.RANDOM,false);
            Player aggressive = new AggressivePlayer("Aggressive player");
            Player controlling = new ControllingPlayer("Controlling player");

            testedPlayer = MCTS;

            Game game = new Game(MCTS, controlling);
            Player winner = game.gamePlay(false);
            if (winner == testedPlayer)
                wins++;
            System.out.println((i+1) + ". " + winner + " WINS THE GAME :)");
        }

        System.out.println("\n" + testedPlayer.getName() + " won " + wins + "/" + iterations + " times");
    }


    public static void main(String args[]) {

        Player randomPlayer = new RandomPlayer("Random player");
        Player MCTSPlayer = new MCTSPlayer("MCTS player",ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM,false);
        Player aggressivePlayer = new AggressivePlayer("Aggressive player");
        Player aggressiveOpponent = new AggressivePlayer("Aggressive opponent");
        Player controlling= new ControllingPlayer("Controlling");


        simpleAccuracyAverageTest(MCTSPlayer,randomPlayer,MCTSPlayer,25,1);

        //playoutNumberImpactTest();
        //iterationsTimeMeassure();

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
                case "MCTSPlayer":first=new MCTSPlayer(firstPlayer.getName(),ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM,false);break;
                case "RandomPlayer":first=new RandomPlayer(firstPlayer.getName());break;
                case "ControllingPlayer":{
                    first=new ControllingPlayer(firstPlayer.getName());
                    break;
                }
                case "AggressivePlayer":first=new AggressivePlayer(firstPlayer.getName());break;
                //default:first=new ControllingPlayer(firstPlayer.getName());
            }
            switch (secondPlayer.getClass().getSimpleName()){
                case "MCTSPlayer":second=new MCTSPlayer(secondPlayer.getName(),ITERATIONS_NUMBER,PLAYOUTS_NUMBER,MCTSPlayoutHeuristic.RANDOM,false);break;
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

    public static void iterationsTimeMeassure() {
        int step=200;
        int min=200;
        int max=400;
        List<Integer> domain= new ArrayList<>();
        List<Long> results=new ArrayList<>();

        Player randomPlayer = new RandomPlayer("Random player");

        for(int i=0;i*step+min<=max;i++)
        {
            Player MCTSPlayer = new MCTSPlayer("MCTS player",i*step+min,100, MCTSPlayoutHeuristic.RANDOM,false);
            domain.add(i*step+min);
            long startTime = System.currentTimeMillis();
            simpleAccuracyTest(MCTSPlayer,randomPlayer,MCTSPlayer,20);
            long endTime = System.currentTimeMillis();
            long timeElapsed=(endTime-startTime)/20;
            System.out.println("ITER: "+i*step+min+" TIME: "+timeElapsed);
            results.add(timeElapsed);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba iteracji");
        plt.ylabel("Średni czas decyzji");
        plt.title("Badanie wpłwyu liczby iteracji na średni czas rozgrywki");
        plt.legend();
        try {
            plt.show();
        }catch (Exception ex){}

        saveToFile("iter_time_"+System.currentTimeMillis(),domain.toArray(),results.toArray());

    }

    public static void saveToFile(String filename, Object [] firstCollection, Object [] secondCollection){
        try{
            FileWriter fileWriter = new FileWriter(filename);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            int size=firstCollection.length;
            for (int i=0;i<size;i++){
                printWriter.println(firstCollection[i]+","+secondCollection[i]);
            }
            printWriter.close();
        }catch (Exception ex){
            System.out.println("FILE SAVING EXCEPTION");
        }

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
            Player MCTSPlayer = new MCTSPlayer("MCTS player",ITERATIONS_NUMBER,min+i*step, MCTSPlayoutHeuristic.RANDOM,false);
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
    * 300 interacji:
    * heuristic 1 vs random 80%
    * random heuristic vs random 80% :)
    * 1000 iteracji:
    * random heuristic vs random 92%
    * heuristic 1 vs random 76% :(
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
