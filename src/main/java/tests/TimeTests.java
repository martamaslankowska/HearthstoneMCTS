package tests;

import com.github.sh0nk.matplotlib4j.Plot;
import game.Game;
import mcts.MCTS;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;
import players.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TimeTests {

    public final static int ITERATIONS_NUMBER=1000;
    public final static int PLAYOUTS_NUMBER=100;

    public static void main(String args[]) {
        //iterationsImpactAllDecisionTimeTest();
        iterationsImpactFirstDecisionTimeTest();
    }

    public static void iterationsImpactFirstDecisionTimeTest() {
        int step=50;
        int min=50;
        int max=3000;
        List<Integer> domain= new ArrayList<>();
        List<Long> results=new ArrayList<>();

        for(int i=0;i*step+min<=max;i++)
        {
            int stepValue=i*step+min;
            domain.add(stepValue);
            long startTime = System.currentTimeMillis();
            for (int j=0;j<100;j++){
                //simpleAccuracyTest(MCTSPlayer,randomPlayer,MCTSPlayer,20);
                Player MCTSPlayer = new MCTSPlayer("MCTS player",stepValue,100, MCTSPlayoutHeuristic.RANDOM,false);
                Player randomPlayer = new RandomPlayer("Random player");
                Node currentRootNode = new Node("0", 0, null, MCTSPlayer, randomPlayer);
                MCTS tree = new MCTS(MCTSPlayer.getName(), 0, currentRootNode,100,MCTSPlayoutHeuristic.RANDOM);
                Node bestChildNode = tree.mcts(stepValue, false);
            }
            long endTime = System.currentTimeMillis();
            long timeElapsed=(endTime-startTime)/100;
            System.out.println("ITER: "+(stepValue)+" TIME: "+timeElapsed);
            results.add(timeElapsed);

        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba iteracji");
        plt.ylabel("Średni czas decyzji");
        plt.title("Wpływ liczby iteracji na średni czas pierwszej decyzji");
        plt.legend();
        long saveTime=System.currentTimeMillis();
        try {
            plt.savefig("tests/results/firstDecTimeIter/iter_time_"+saveTime+".png");
            plt.show();
        }catch (Exception ex){}

        Tests.saveToFile("tests/results/firstDecTimeIter/iter_time_"+saveTime,domain.toArray(),results.toArray());
    }

    public static void iterationsImpactAllDecisionTimeTest() {
        int step=500;
        int min=500;
        int max=5000;
        List<Integer> domain= new ArrayList<>();
        List<Long> results=new ArrayList<>();

        for(int i=0;i*step+min<=max;i++)
        {
            int stepValue=i*step+min;
            domain.add(stepValue);
            long cululatedTime=0;
            for (int j=0;j<20;j++){
                //simpleAccuracyTest(MCTSPlayer,randomPlayer,MCTSPlayer,20);
                Player MCTSPlayer = new MCTSPlayer("MCTS player",stepValue,100, MCTSPlayoutHeuristic.RANDOM,false);
                Player randomPlayer = new RandomPlayer("Random player");
                Game game = new Game(MCTSPlayer, randomPlayer);
                Player winner = game.gamePlay(false);
                List<Long> decisonTimes=game.decisionTimes;
                //System.out.println(decisonTimes);
                long sum =0;
                for (Long decisonTime:decisonTimes
                     ) {
                    sum+=decisonTime;
                }
                cululatedTime+=(sum/decisonTimes.size());
            }
            long timeElapsed=cululatedTime/20;
            System.out.println("ITER: "+(stepValue)+" TIME: "+timeElapsed);
            results.add(timeElapsed);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba iteracji");
        plt.ylabel("Średni czas decyzji");
        plt.title("Wpływ liczby iteracji na średni czas wszystkich decyzji gracza");
        plt.legend();
        long saveTime=System.currentTimeMillis();
        try {
            plt.savefig("tests/results/allDecTimeIter/iter_time_"+saveTime+".png");
            plt.show();
        }catch (Exception ex){}


        Tests.saveToFile("tests/results/allDecTimeIter/iter_time_"+saveTime,domain.toArray(),results.toArray());
    }



    public static void playoutNumberFirstDecisionTimeTest() {
        int step=10;
        int min=10;
        int max=700;
        List<Integer> domain= new ArrayList<>();
        List<Long> results=new ArrayList<>();

        //Player randomPlayer = new RandomPlayer("Random player");

        for(int i=0;i*step+min<=max;i++)
        {
            int stepValue=i*step+min;
            domain.add(stepValue);
            long startTime = System.currentTimeMillis();
            for (int j=0;j<100;j++){
                Player MCTSPlayer = new MCTSPlayer("MCTS player",stepValue,100, MCTSPlayoutHeuristic.RANDOM,false);
                Player randomPlayer = new RandomPlayer("Random player");
                Node currentRootNode = new Node("0", 0, null, MCTSPlayer, randomPlayer);
                MCTS tree = new MCTS(MCTSPlayer.getName(), 0, currentRootNode,stepValue,MCTSPlayoutHeuristic.RANDOM);
                Node bestChildNode = tree.mcts(1000, false);
            }
            long endTime = System.currentTimeMillis();
            long timeElapsed=(endTime-startTime)/100;
            System.out.println("PLAYOUTS: "+stepValue+" TIME: "+timeElapsed);
            results.add(timeElapsed);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba playoutów");
        plt.ylabel("Średni czas pierwszej decyzji");
        plt.title("Wpływ liczby playoutów na czas pierwszej decyzji");
        plt.legend();
        long saveTime=System.currentTimeMillis();
        try {
            plt.savefig("tests/results/firstDecTimePlayouts/playout_time_"+saveTime+".png");
            plt.show();
        }catch (Exception ex){}



        Tests.saveToFile("tests/results/firstDecTimePlayouts/playout_time_"+saveTime,domain.toArray(),results.toArray());

    }

    public static void playoutNumberAllDecisionTimeTest() {
        int step=10;
        int min=10;
        int max=700;
        List<Integer> domain= new ArrayList<>();
        List<Long> results=new ArrayList<>();

        //Player randomPlayer = new RandomPlayer("Random player");

        for(int i=0;i*step+min<=max;i++)
        {
            int stepValue=i*step+min;
            domain.add(stepValue);
            long cululatedTime=0;
            for (int j=0;j<100;j++){
                //simpleAccuracyTest(MCTSPlayer,randomPlayer,MCTSPlayer,20);
                Player MCTSPlayer = new MCTSPlayer("MCTS player",1000,stepValue, MCTSPlayoutHeuristic.RANDOM,false);
                Player randomPlayer = new RandomPlayer("Random player");
                Game game = new Game(MCTSPlayer, randomPlayer);
                Player winner = game.gamePlay(false);
                List<Long> decisonTimes=game.decisionTimes;
                //System.out.println(decisonTimes);
                long sum =0;
                for (Long decisonTime:decisonTimes
                ) {
                    sum+=decisonTime;
                }
                cululatedTime+=(sum/decisonTimes.size());
            }
            long timeElapsed=cululatedTime/100;
            System.out.println("PLAYOUTS: "+stepValue+" TIME: "+timeElapsed);
            results.add(timeElapsed);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Liczba playoutów");
        plt.ylabel("Średni czas pierwszej decyzji");
        plt.title("Wpływ liczby playoutów na średni czas wszytskich decyzji");
        plt.legend();
        long saveTime=System.currentTimeMillis();
        try {
            plt.savefig("tests/results/allDecTimePlayouts/playout_time_"+saveTime+".png");
            plt.show();
        }catch (Exception ex){}


        Tests.saveToFile("tests/results/allDecTimePlayouts/playout_time_"+saveTime,domain.toArray(),results.toArray());

    }



}
