package players;

import attacks.Attack;
import game.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayer extends Player {

    private static Random random = new Random();

    public RandomPlayer(String name) {
        super(name);
    }

    @Override
    public List<List<Attack>> getPossibleAttacks() {
        return null;
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        if (!possibleCardsToPlay.isEmpty())
            return possibleCardsToPlay.get(random.nextInt(possibleCardsToPlay.size()));
        else
            return new ArrayList<Card>();
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        return null;
    }

    public RandomPlayer(RandomPlayer other) {
        super(other);
    }



}
