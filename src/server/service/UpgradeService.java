package server.service;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;
import server.model.player.Player;

public class UpgradeService {
    public static final int CHARACTER_MAX_LEVEL = 100;
    public static final int ITEM_MAX_LEVEL = 20;

    public UpgradeCost getCharacterCost(Character character) {
        int next = character.getLevel() + 1;
        int rarityScale = rarityScale(character.getRarity());
        return new UpgradeCost(60 + next * 2 * rarityScale, 4 + Math.max(1, next / 4) * rarityScale);
    }

    public UpgradeCost getItemCost(InventoryItem item) {
        int next = item.getLevel() + 1;
        int rarityScale = rarityScale(item.getRarity());
        return new UpgradeCost(35 + next * 2 * rarityScale, 3 + Math.max(1, next / 5) * rarityScale);
    }

    public String upgradeCharacter(Player player, Character character) {
        if (character == null) return "Choose a character first.";
        if (character.getLevel() >= CHARACTER_MAX_LEVEL) return character.getName() + " is already max level.";
        UpgradeCost cost = getCharacterCost(character);
        if (!player.spendUpgradeCurrencies(cost.commonCost, cost.rareCost)) {
            return "Not enough materials. Need " + cost + ". You have C " + player.getCommonCurrency() + " / R " + player.getRareCurrency() + ".";
        }
        character.setLevel(character.getLevel() + 1);
        return "Upgraded " + character.getName() + " to Lv" + character.getLevel() + "! Spent " + cost + ".";
    }

    public String upgradeItem(Player player, InventoryItem item) {
        if (item == null) return "Choose a weapon/item first.";
        if (item.getLevel() >= ITEM_MAX_LEVEL) return item.getName() + " is already max level.";
        UpgradeCost cost = getItemCost(item);
        if (!player.spendUpgradeCurrencies(cost.commonCost, cost.rareCost)) {
            return "Not enough materials. Need " + cost + ". You have C " + player.getCommonCurrency() + " / R " + player.getRareCurrency() + ".";
        }
        item.setLevel(item.getLevel() + 1);
        return "Enhanced " + item.getName() + " to Lv" + item.getLevel() + "! Spent " + cost + ".";
    }

    private int rarityScale(Rarity rarity) {
        return switch (rarity) {
            case LEGENDARY -> 3;
            case RARE -> 2;
            case COMMON -> 1;
        };
    }

    public static class UpgradeCost {
        public final int commonCost;
        public final int rareCost;
        public UpgradeCost(int commonCost, int rareCost) {
            this.commonCost = commonCost;
            this.rareCost = rareCost;
        }
        @Override
        public String toString() {
            return commonCost + " C + " + rareCost + " R";
        }
    }
}
