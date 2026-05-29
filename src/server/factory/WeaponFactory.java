package server.factory;

import server.model.abstracts.InventoryItem;
import server.model.items.Weapon;
import server.model.enums.Rarity;
import server.model.enums.WeaponType;

import java.util.Random;

public class WeaponFactory implements SummonFactory<InventoryItem> {
    private Random random = new Random();

    private static final String[] WEAPON_NAMES = {
        "Dragon Slayer", "Excalibur", "Storm Breaker", "Shadow Fang", "Holy Avenger",
        "Moonlight Blade", "Thunder Edge", "Phoenix Talon", "Void Reaper", "Star Piercer"
    };
    private static final String[] PASSIVE_SKILLS = {
        "Critical Boost", "Elemental Damage Up", "Attack Speed Up",
        "Life Steal", "Armor Penetration", "Chain Lightning",
        "Burning Strike", "Frost Bite"
    };

    @Override
    public InventoryItem create(Rarity rarity) {
        WeaponType type = WeaponType.values()[random.nextInt(WeaponType.values().length)];
        int baseDmg = type.getBaseDamage() + random.nextInt(50);
        int level = Math.max(rarity.getMinLevel(), random.nextInt(90) + 1);
        String name = WEAPON_NAMES[random.nextInt(WEAPON_NAMES.length)];
        String passive = PASSIVE_SKILLS[random.nextInt(PASSIVE_SKILLS.length)];
        return new Weapon(name, rarity, level, type, baseDmg, passive);
    }
}
