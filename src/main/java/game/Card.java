package game;

import attacks.ITargetWhileAttack;

import java.util.Objects;

public class Card implements ITargetWhileAttack {

    private int id;
    private String name;
    private int mana;
    private int attack;
    private int hp;
    private boolean beforeAttack;  // indicates whether warrior has attacked yet in this round or is he able to attack


    public Card(String name, int mana, int attack, int hp) {
        this.name = name;
        this.mana = mana;
        this.attack = attack;
        this.hp = hp;
        this.beforeAttack = true;
    }

    public Card(int id, String name, int mana, int attack, int hp) {
        this(name, mana, attack, hp);
        this.id = id;
    }

    public Card(Card other) {
        this.id = other.id;
        this.name = other.name;
        this.mana = other.mana;
        this.attack = other.attack;
        this.hp = other.hp;
        this.beforeAttack = other.beforeAttack;
    }

    public Card(Card other, int id) {
        this(other);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isBeforeAttack() {
        return beforeAttack;
    }

    public void setBeforeAttack(boolean beforeAttack) {
        this.beforeAttack = beforeAttack;
    }

    @Override
    public String toString() {
        return name + " M:(" + mana + "\u27E1) A:" + attack + "\u2694 HP:" + hp + "\u2661";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id &&
                mana == card.mana &&
                attack == card.attack &&
                name.equals(card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, mana, attack);
    }
}
