package game;

public class Card {

    private static int ID = 0;
    private int id;
    private String name;
    private int mana;
    private int attack;
    private int hp;


    public Card(String name, int mana, int attack, int hp) {
        this.id = ID++;
        this.name = name;
        this.mana = mana;
        this.attack = attack;
        this.hp = hp;
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


    @Override
    public String toString() {
        return name + " (" + mana + ") " + attack + "/" + hp;
    }
}
