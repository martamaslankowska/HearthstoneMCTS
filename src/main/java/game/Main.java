package game;

import mcts.MCTSPlayoutHeuristic;
import players.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    public static List<Card> CARDS = Arrays.asList(
        new Card("Arcanite Reaper", 5, 5, 2),
        new Card("Bloodfen Raptor", 2, 3, 2),
//        new Card("Boulderfist Ogre", 6, 6, 7),
        new Card("Chillwind Yeti", 4, 4, 5),
//        new Card("Core Hound", 7, 9, 5),
        new Card("Fiery War Axe", 3, 3, 2),
        new Card("Light's Justice", 1, 1, 4),
        new Card("Magma Rager", 3, 5, 1),
        new Card("Murloc Raider", 1, 2, 1),
        new Card("Oasis Snapjaw", 4, 2, 7),
        new Card("Spider Tank", 3, 3, 4),
        new Card("River Crocolisk", 2, 2, 3)
    );

    public static Random random = new Random();


    public static void main(String args[]) {
        Player random = new RandomPlayer("Random player");
        Player MCTS = new MCTSPlayer("MCTS player",1000,100, MCTSPlayoutHeuristic.RANDOM);
        Player aggressive = new AggressivePlayer("Aggressive player");
        Player controlling= new ControllingPlayer("Controlling player");

        Game game = new Game(controlling, random);
        Player winner = game.gamePlay(true);
        System.out.println("\n" + winner + " WINS THE GAME :)");

    }

}
