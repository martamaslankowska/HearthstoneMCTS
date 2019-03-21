package attacks;

import game.Card;
import players.Player;

public class WarriorAttack extends Attack {

    public WarriorAttack(Card attacker, Card target) {
        super(attacker, target);
    }

    @Override
    public Card attackerAfterAttack() {
        if (attackerDies())
            return null;
        else
            return new Card(attacker.getId(), attacker.getName(), attacker.getMana(), attacker.getAttack(),
                    attacker.getHp() - ((Card)target).getAttack());
    }

    @Override
    public boolean attackerDies() {
        return attacker.getHp() <= ((Card)target).getAttack();
    }

    @Override
    public Card targetAfterAttack() {
        if (targetDies())
            return null;
        else {
            return new Card(((Card)target).getId(), target.getName(), ((Card)target).getMana(), ((Card)target).getAttack(),
                    target.getHp() - attacker.getAttack());
        }
    }

}
