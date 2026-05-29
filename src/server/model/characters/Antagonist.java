package server.model.characters;

import server.model.abstracts.Character;
import server.model.enums.Rarity;
import server.model.enums.Element;

public class Antagonist extends Character {
    private String bossTitle;
    private boolean isRaidBoss;

    public Antagonist(String name, Rarity rarity, Element element, int level, String bossTitle, boolean isRaidBoss) {
        super(name, rarity, element, level);
        this.bossTitle = bossTitle;
        this.isRaidBoss = isRaidBoss;
    }

    @Override
    public String generateReport() {
        return super.generateReport() +
            "\n  Title: " + bossTitle + " | Raid Boss: " + (isRaidBoss ? "Yes" : "No") +
            " | Effective HP: " + getEffectiveHP();
    }

    public int getEffectiveHP() {
        return isRaidBoss ? hp * 3 : hp;
    }

    public String getBossTitle() { return bossTitle; }
    public boolean isRaidBoss() { return isRaidBoss; }
}
