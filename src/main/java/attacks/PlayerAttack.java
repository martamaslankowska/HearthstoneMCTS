package attacks;

import game.Card;
import players.Player;
import players.RandomPlayer;

public class PlayerAttack extends Attack {

    public PlayerAttack(Card attacker, ITargetWhileAttack target) {
        super(attacker, target);
    }

//    @Override
//    public Card attackerAfterAttack() {
//        return attacker;
//    }

    @Override
    public boolean attackerDies() {
        return false;
    }

//    @Override
//    public Player targetAfterAttack() {
//        if (targetDies())
//            return null;
//        else {
//            if (target instanceof RandomPlayer) {
//                Player targetAfterAttack = new RandomPlayer(target.getName());
//                targetAfterAttack.setHp(target.getHp() - attacker.getAttack());
//                return targetAfterAttack;
//            }
//            else
//                return null;
//        }
//    }
}
