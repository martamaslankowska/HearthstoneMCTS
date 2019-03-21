package attacks;

import game.Card;

public abstract class Attack {

    protected Card attacker;
    protected ITargetWhileAttack target;

    public Attack(Card attacker, Card target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public String toString() {
        return attacker + " -/-> " + target;
    }

    public Card getAttacker() {
        return attacker;
    }

    public void setAttacker(Card attacker) {
        this.attacker = attacker;
    }

    public ITargetWhileAttack getTarget() {
        return target;
    }

    public void setTarget(ITargetWhileAttack target) {
        this.target = target;
    }


    public abstract Card attackerAfterAttack();

    public abstract boolean attackerDies();

    public abstract ITargetWhileAttack targetAfterAttack();

    public boolean targetDies() {
        return target.getHp() <= attacker.getAttack();
    }

}



