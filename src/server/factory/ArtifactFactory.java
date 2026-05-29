package server.factory;

import server.model.abstracts.InventoryItem;
import server.model.items.Artifact;
import server.model.enums.Rarity;

import java.util.Random;

public class ArtifactFactory implements SummonFactory<InventoryItem> {
    private Random random = new Random();

    private static final String[] SET_NAMES = {
        "Crimson Witch", "Noblesse Oblige", "Gladiators Finale",
        "Viridescent Venerer", "Thundering Fury", "Blizzard Strayer"
    };
    private static final String[] SLOTS = {"Flower", "Plume", "Sands", "Goblet", "Circlet"};

    @Override
    public InventoryItem create(Rarity rarity) {
        String set = SET_NAMES[random.nextInt(SET_NAMES.length)];
        String slot = SLOTS[random.nextInt(SLOTS.length)];
        int level = Math.max(rarity.getMinLevel(), random.nextInt(20) + 1);
        String name = set + " " + slot;
        return new Artifact(name, rarity, level, set, slot);
    }
}
