package attacks;

import game.Card;
import players.Player;

public class PlayerAttack extends Attack {

    public PlayerAttack(Card attacker, Card target) {
        super(attacker, target);
    }

    @Override
    public Card attackerAfterAttack() {
        return attacker;
    }

    @Override
    public boolean attackerDies() {
        return false;
    }

    @Override
    public Player targetAfterAttack() {
        if (targetDies())
            return null;
        else {
            System.out.println(":(");
            return null;
        }
    }
}
