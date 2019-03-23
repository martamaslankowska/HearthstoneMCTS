package players;

import attacks.Attack;
import game.Card;

import java.util.ArrayList;
import java.util.List;

public class MCTSPlayer extends Player {

    private MCTSPlayer() {}

    public MCTSPlayer(String name) {
        super(name);
    }

    @Override
    public Player deepCopy() {
        Player res = new MCTSPlayer();
        return res.modify(res);
    }

    @Override
    public List<List<Attack>> getPossibleAttacks(Player opponent) {
        return null;
    }

    @Override
    public List<Card> selectCardsToPlay(List<List<Card>> possibleCardsToPlay) {
        return null;
    }

    @Override
    public List<Attack> selectAttacksToPlay(Player opponent, List<List<Attack>> possibleAttacks) {
        return null;
    }


}
