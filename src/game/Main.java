package game;

import players.Player;
import players.RandomPlayer;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Card> CARDS = new ArrayList<Card>() {
        {
            add(new Card("Arcanite Reaper", 5, 5, 2));
            add(new Card("Bloodfen Raptor", 2, 3, 2));
            add(new Card("Boulderfist Ogre", 6, 6, 7));
            add(new Card("Chillwind Yeti", 4, 4, 5));
            add(new Card("Core Hound", 7, 9, 5));
            add(new Card("Fiery War Axe", 3, 3, 2));
            add(new Card("Light's Justice", 1, 1, 4));
            add(new Card("Magma Rager", 3, 5, 1));
            add(new Card("Murloc Raider", 1, 2, 1));
            add(new Card("Oasis Snapjaw", 4, 2, 7));
        }
    };


    public static void main(String args[]) {
        Player firstPlayer = new RandomPlayer("Random player ONE");
        Player secondPlayer = new RandomPlayer("Random player TWO");

        Game game = new Game(firstPlayer, secondPlayer);
        game.gamePlay(true);

    }

}
