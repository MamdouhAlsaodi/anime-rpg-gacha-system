package server.model.abstracts;

import server.model.enums.Rarity;

public abstract class InventoryItem extends BaseEntity {
    protected String name;
    protected Rarity rarity;
    protected int level;

    public InventoryItem(String name, Rarity rarity, int level) {
        super();
        this.name = name;
        this.rarity = rarity;
        this.level = level;
    }

    public abstract String getItemType();

    @Override
    public String describeYourself() {
        return "[" + getItemType() + "] " + name + " | Rarity: " + rarity + " | Level: " + level;
    }

    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }
    public void setName(String name) { this.name = name; }
}
