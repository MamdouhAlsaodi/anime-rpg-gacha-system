package server.model.items;

import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;
import server.model.enums.WeaponType;

public class Weapon extends InventoryItem {
    private WeaponType weaponType;
    private int baseDamage;
    private String passiveSkill;

    public Weapon(String name, Rarity rarity, int level, WeaponType weaponType, int baseDamage, String passiveSkill) {
        super(name, rarity, level);
        this.weaponType = weaponType;
        this.baseDamage = baseDamage;
        this.passiveSkill = passiveSkill;
    }

    @Override
    public String getItemType() { return "Weapon"; }

    public int getEffectiveDamage() {
        int rarityBonus = switch (rarity) {
            case LEGENDARY -> 100;
            case RARE -> 40;
            case COMMON -> 10;
        };
        return baseDamage + (level * 5) + rarityBonus;
    }

    @Override
    public String describeYourself() {
        return "[Weapon] " + name + " | " + rarity + " | " + weaponType.getDisplayName() +
            " | DMG: " + getEffectiveDamage() + " | Passive: " + passiveSkill;
    }

    public WeaponType getWeaponType() { return weaponType; }
    public int getBaseDamage() { return baseDamage; }
    public String getPassiveSkill() { return passiveSkill; }
}
