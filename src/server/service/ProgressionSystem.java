package server.service;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;

public class ProgressionSystem {

    public void levelUp(Character c, int levels) {
        c.setLevel(c.getLevel() + levels);
    }

    public void enhanceItem(InventoryItem item, int levels) {
        item.setLevel(item.getLevel() + levels);
    }

    public int calculatePowerLevel(Character c) {
        return (int)(c.getHp() + c.getAttack() * 2 + c.getDefense() * 1.5);
    }

    public String getCharacterRank(Character c) {
        int power = calculatePowerLevel(c);
        if (power >= 5000) return "S";
        if (power >= 3000) return "A";
        if (power >= 1500) return "B";
        return "C";
    }
}
