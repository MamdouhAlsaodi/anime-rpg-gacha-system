package server.model.characters;

import server.model.abstracts.Character;
import server.model.enums.Rarity;
import server.model.enums.Element;
import server.model.enums.WeaponType;

public class Hero extends Character {
    private WeaponType weaponType;
    private int constellation;

    public Hero(String name, Rarity rarity, Element element, int level, WeaponType weaponType) {
        super(name, rarity, element, level);
        this.weaponType = weaponType;
        this.constellation = 0;
    }

    @Override
    public String generateReport() {
        return super.generateReport() +
            "\n  Weapon: " + weaponType.getDisplayName() +
            " | Constellation: C" + constellation;
    }

    public void upgradeConstellation() {
        this.constellation++;
    }

    public WeaponType getWeaponType() { return weaponType; }
    public int getConstellation() { return constellation; }
}
