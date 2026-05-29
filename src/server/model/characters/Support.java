package server.model.characters;

import server.model.abstracts.Character;
import server.model.enums.Rarity;
import server.model.enums.Element;

public class Support extends Character {
    private String healType;
    private double healMultiplier;

    public Support(String name, Rarity rarity, Element element, int level, String healType, double healMultiplier) {
        super(name, rarity, element, level);
        this.healType = healType;
        this.healMultiplier = healMultiplier;
    }

    @Override
    public String generateReport() {
        return super.generateReport() +
            "\n  Heal Type: " + healType + " | Heal Amount: " + calculateHeal();
    }

    public int calculateHeal() {
        return (int)(attack * healMultiplier);
    }

    public String getHealType() { return healType; }
    public double getHealMultiplier() { return healMultiplier; }
}
