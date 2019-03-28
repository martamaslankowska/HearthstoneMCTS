package players;

import attacks.Attack;
import game.Card;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class PlayerToAttack extends Player {

    public PlayerToAttack(String name) {
        super(name);
    }

    @Override
    public Player deepCopy() {
        return null;
    }

    public PlayerToAttack(String name, int hp) {
        super(name);
        this.hp = hp;
    }

    @Override
    public List<List<Attack>> getPossibleAttacks(Player opponent, int move) {
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
