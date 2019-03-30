package tests;

import com.github.sh0nk.matplotlib4j.Plot;
import mcts.MCTS;
import mcts.MCTSPlayoutHeuristic;
import mcts.Node;
import players.MCTSPlayer;
import players.Player;
import players.RandomPlayer;

import java.util.ArrayList;
import java.util.List;

public class CTests {
    //Skuteczność przy 100 zagrywkach w zależności od parametru C

    public static void main(String args[]) {
        cTest();
    }

    public static void cTest() {
        double step=0.2;
        double min=0.2;
        double max=1;
        List<Double> domain= new ArrayList<>();
        List<Double> results=new ArrayList<>();

        //Player randomPlayer = new RandomPlayer("Random player");

        for(int i=0;i*step+min<=max;i++)
        {
            double stepValue=i*step+min;
            domain.add(stepValue);
            MCTS.C=stepValue;
            Player random = new RandomPlayer("Random player");
            Player MCTS = new MCTSPlayer("MCTS player", 1000, 100, MCTSPlayoutHeuristic.RANDOM,false);
            double result=Tests.simpleAccuracyAverageTest(MCTS,random,MCTS,20,1);
            System.out.println("C: "+stepValue+" TIME: "+result);
            results.add(result);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(domain,results);
        plt.xlabel("Wartość stałej C");
        plt.ylabel("Skuteczność");
        plt.title("Wpływ stałej C na wyniki gracza MCTS");
        plt.legend();
        long saveTime=System.currentTimeMillis();
        try {
            plt.savefig("tests/results/cResults/c_impact_"+saveTime+".png");
            plt.show();
        }catch (Exception ex){}



        Tests.saveToFile("tests/results/cResults/c_impact_"+saveTime,domain.toArray(),results.toArray());

    }
}
