package server.service;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;
import server.model.player.Player;
import server.factory.CharacterFactory;
import server.factory.WeaponFactory;
import server.factory.ArtifactFactory;
import server.exception.InsufficientGemsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GachaSystem {
    private Random random = new Random();
    private static final double LEGENDARY_RATE = 0.006;
    private static final double RARE_RATE = 0.051;
    private static final int HARD_PITY = 90;
    private static final int SOFT_PITY = 10;
    private static final int SINGLE_COST = 160;

    public Object summonSingle(Player player, CharacterFactory cf, WeaponFactory wf, ArtifactFactory af)
            throws InsufficientGemsException {
        player.spendGems(SINGLE_COST);
        Rarity rarity = rollRarity(player);
        player.incrementPity();

        boolean giveCharacter = random.nextDouble() < 0.55;
        Object result;
        if (giveCharacter) {
            result = cf.create(rarity);
        } else {
            int itemType = random.nextInt(3);
            if (itemType == 0) {
                result = wf.create(rarity);
            } else {
                result = af.create(rarity);
            }
        }

        if (rarity == Rarity.LEGENDARY) {
            player.resetHardPity();
            player.resetSoftPity();
        } else if (rarity == Rarity.RARE) {
            player.resetSoftPity();
        }

        return result;
    }

    public List<Object> summonTen(Player player, CharacterFactory cf, WeaponFactory wf, ArtifactFactory af)
            throws InsufficientGemsException {
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(summonSingle(player, cf, wf, af));
        }
        return results;
    }

    private Rarity rollRarity(Player player) {
        if (player.getPityCounter() >= HARD_PITY - 1) {
            return Rarity.LEGENDARY;
        }
        if (player.getSoftPityCounter() >= SOFT_PITY - 1) {
            return Rarity.RARE;
        }
        double legendaryRate = LEGENDARY_RATE;
        if (player.getPityCounter() > 70) {
            legendaryRate += (player.getPityCounter() - 70) * 0.01;
        }
        double roll = random.nextDouble();
        if (roll < legendaryRate) {
            return Rarity.LEGENDARY;
        } else if (roll < legendaryRate + RARE_RATE) {
            return Rarity.RARE;
        }
        return Rarity.COMMON;
    }
}
