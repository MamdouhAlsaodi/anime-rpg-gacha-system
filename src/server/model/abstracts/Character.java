package server.model.abstracts;

import server.model.enums.Rarity;
import server.model.enums.Element;

public abstract class Character extends BaseEntity {
    protected String name;
    protected Rarity rarity;
    protected Element element;
    protected int level;
    protected int hp;
    protected int attack;
    protected int defense;

    public Character(String name, Rarity rarity, Element element, int level) {
        super();
        this.name = name;
        this.rarity = rarity;
        this.element = element;
        this.level = level;
        this.hp = level * 100;
        this.attack = level * 10 + (rarity == Rarity.LEGENDARY ? 50 : rarity == Rarity.RARE ? 20 : 0);
        this.defense = level * 5;
    }

    public String generateReport() {
        return "Name: " + name + " | Rarity: " + rarity + " | Element: " + element.getDisplayName() +
            "\n  Level: " + level + " | HP: " + hp + " | ATK: " + attack + " | DEF: " + defense;
    }

    @Override
    public String describeYourself() { return generateReport(); }

    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
    public Element getElement() { return element; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }

    public void setLevel(int level) {
        this.level = level;
        this.hp = level * 100;
        this.attack = level * 10 + (rarity == Rarity.LEGENDARY ? 50 : rarity == Rarity.RARE ? 20 : 0);
        this.defense = level * 5;
    }
}
