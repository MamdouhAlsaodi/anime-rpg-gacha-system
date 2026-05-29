package server.model.items;

import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;

public class Artifact extends InventoryItem {
    private String setBonus;
    private String slot;

    public Artifact(String name, Rarity rarity, int level, String setBonus, String slot) {
        super(name, rarity, level);
        this.setBonus = setBonus;
        this.slot = slot;
    }

    @Override
    public String getItemType() { return "Artifact"; }

    public int getStatBonus() {
        return level * 3 + rarity.ordinal() * 10;
    }

    @Override
    public String describeYourself() {
        return "[Artifact] " + name + " | " + rarity + " | " + setBonus +
            " | Slot: " + slot + " | Bonus: +" + getStatBonus();
    }

    public String getSetBonus() { return setBonus; }
    public String getSlot() { return slot; }
}
