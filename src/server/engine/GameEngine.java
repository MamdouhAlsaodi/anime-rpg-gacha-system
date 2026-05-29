package server.engine;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.characters.Hero;
import server.model.player.Player;
import server.model.player.Inventory;
import server.model.enums.Rarity;
import server.service.*;
import server.factory.CharacterFactory;
import server.factory.WeaponFactory;
import server.factory.ArtifactFactory;
import server.exception.InsufficientGemsException;
import server.exception.DuplicateCharacterException;
import server.exception.InventoryFullException;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private Player player;
    private Inventory inventory;
    private GachaSystem gachaSystem;
    private InventoryManager inventoryManager;
    private ConstellationSystem constellationSystem;
    private ProgressionSystem progressionSystem;
    private ResourceSystem resourceSystem;
    private CharacterFactory characterFactory;
    private WeaponFactory weaponFactory;
    private ArtifactFactory artifactFactory;
    private int legendaryCount;
    private int rareCount;
    private int commonCount;
    private int totalPulls;
    private int constellationUpgrades;

    public GameEngine() {
        this.player = new Player("Player", 10000);
        this.inventory = new Inventory();
        this.gachaSystem = new GachaSystem();
        this.inventoryManager = new InventoryManager();
        this.constellationSystem = new ConstellationSystem();
        this.progressionSystem = new ProgressionSystem();
        this.resourceSystem = new ResourceSystem(player);
        this.characterFactory = new CharacterFactory();
        this.weaponFactory = new WeaponFactory();
        this.artifactFactory = new ArtifactFactory();
        this.legendaryCount = 0;
        this.rareCount = 0;
        this.commonCount = 0;
        this.totalPulls = 0;
        this.constellationUpgrades = 0;
    }

    public GameEngine(String playerName, int startingGems) {
        this();
        this.player = new Player(playerName, startingGems);
        this.resourceSystem = new ResourceSystem(this.player);
    }

    public List<Object> performSummon(int count) throws InsufficientGemsException {
        if (count == 1) {
            List<Object> result = new ArrayList<>();
            Object summoned = gachaSystem.summonSingle(player, characterFactory, weaponFactory, artifactFactory);
            result.add(summoned);
            trackRarity(summoned);
            Rarity r = getRarity(summoned);
            resourceSystem.onSummon(1, r);
            addToInventory(summoned);
            totalPulls++;
            checkSummonAchievements();
            return result;
        } else {
            List<Object> results = gachaSystem.summonTen(player, characterFactory, weaponFactory, artifactFactory);
            for (Object obj : results) {
                trackRarity(obj);
                Rarity r = getRarity(obj);
                resourceSystem.onSummon(1, r);
                addToInventory(obj);
            }
            totalPulls += results.size();
            checkSummonAchievements();
            return results;
        }
    }

    private Rarity getRarity(Object obj) {
        if (obj instanceof Character c) return c.getRarity();
        if (obj instanceof InventoryItem item) return item.getRarity();
        return Rarity.COMMON;
    }

    private void checkSummonAchievements() {
        resourceSystem.checkAndUnlockAchievement("First Summon", totalPulls >= 1);
        resourceSystem.checkAndUnlockAchievement("Summon 10 characters", totalPulls >= 10);
        resourceSystem.checkAndUnlockAchievement("Summon 50 characters", totalPulls >= 50);
        resourceSystem.checkAndUnlockAchievement("Summon 100 characters", totalPulls >= 100);
        resourceSystem.checkAndUnlockAchievement("Summon 500 characters", totalPulls >= 500);
        resourceSystem.checkAndUnlockAchievement("Get first Legendary", legendaryCount >= 1);
        resourceSystem.checkAndUnlockAchievement("Collect 5 Legendaries", legendaryCount >= 5);
        resourceSystem.checkAndUnlockAchievement("Collect 20 Legendaries", legendaryCount >= 20);
        int used = inventory.getCharacterCount() + inventory.getItemCount();
        resourceSystem.checkAndUnlockAchievement("Fill inventory 50%", used >= inventory.getCapacity() / 2);
        resourceSystem.checkAndUnlockAchievement("Fill inventory 100%", used >= inventory.getCapacity());
    }

    private void trackRarity(Object obj) {
        Rarity r = getRarity(obj);
        switch (r) {
            case LEGENDARY -> legendaryCount++;
            case RARE -> rareCount++;
            case COMMON -> commonCount++;
        }
    }

    private void addToInventory(Object obj) {
        if (obj instanceof Character c) {
            try {
                inventory.addCharacter(c);
            } catch (DuplicateCharacterException e) {
                handleDuplicateCharacter(c);
            } catch (InventoryFullException e) {
                System.out.println("  [!] Inventory full - " + c.getName() + " discarded");
            }
        } else if (obj instanceof InventoryItem item) {
            try {
                inventory.addItem(item);
            } catch (InventoryFullException e) {
                System.out.println("  [!] Inventory full - " + item.getName() + " discarded");
            }
        }
    }

    private void handleDuplicateCharacter(Character newChar) {
        for (Character existing : inventory.getAllCharacters()) {
            if (existing.getName().equals(newChar.getName()) && existing instanceof Hero hero) {
                String result = constellationSystem.processDuplicate(inventory, hero, newChar);
                System.out.println("  >> " + result);
                constellationUpgrades++;
                resourceSystem.onConstellation();
                resourceSystem.checkAndUnlockAchievement("First Constellation", constellationUpgrades >= 1);
                resourceSystem.checkAndUnlockAchievement("10 Constellations", constellationUpgrades >= 10);
                resourceSystem.checkAndUnlockAchievement("50 Constellations", constellationUpgrades >= 50);
                return;
            }
        }
    }

    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= PLAYER STATS =======\n");
        sb.append(player.toString()).append("\n");
        sb.append("\n======= PULL STATS =======\n");
        sb.append(String.format("Total Pulls: %d%n", totalPulls));
        sb.append(String.format("Legendaries: %d (%.2f%%)%n", legendaryCount, totalPulls > 0 ? (legendaryCount * 100.0 / totalPulls) : 0));
        sb.append(String.format("Rares: %d (%.2f%%)%n", rareCount, totalPulls > 0 ? (rareCount * 100.0 / totalPulls) : 0));
        sb.append(String.format("Commons: %d (%.2f%%)%n", commonCount, totalPulls > 0 ? (commonCount * 100.0 / totalPulls) : 0));
        sb.append(String.format("Constellation Upgrades: %d%n", constellationUpgrades));
        sb.append("\n").append(resourceSystem.getResourceSummary());
        sb.append("\n").append(resourceSystem.getDailyMissionsDisplay());
        sb.append("\n======= INVENTORY =======\n");
        sb.append(String.format("Characters: %d | Items: %d | Used: %d/%d%n",
            inventory.getCharacterCount(), inventory.getItemCount(),
            inventory.getCharacterCount() + inventory.getItemCount(), inventory.getCapacity()));
        return sb.toString();
    }

    public String claimDailyLogin() { return resourceSystem.claimDailyLogin(); }
    public String claimQuestReward(String type) { return resourceSystem.claimQuestReward(type); }
    public String convertDuplicates(int count) { return resourceSystem.convertDuplicateToGems(count); }

    public Inventory getInventory() { return inventory; }
    public Player getPlayer() { return player; }
    public GachaSystem getGachaSystem() { return gachaSystem; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public ConstellationSystem getConstellationSystem() { return constellationSystem; }
    public ProgressionSystem getProgressionSystem() { return progressionSystem; }
    public ResourceSystem getResourceSystem() { return resourceSystem; }
    public int getLegendaryCount() { return legendaryCount; }
    public int getRareCount() { return rareCount; }
    public int getCommonCount() { return commonCount; }
    public int getTotalPulls() { return totalPulls; }
    public int getConstellationUpgrades() { return constellationUpgrades; }

    public void levelUpCharacter(String id, int levels) {
        Character c = inventory.getCharacter(id);
        if (c != null) {
            progressionSystem.levelUp(c, levels);
            resourceSystem.onLevelUp();
        }
    }

    public void enhanceItem(String id, int levels) {
        InventoryItem item = inventory.getItem(id);
        if (item != null) {
            progressionSystem.enhanceItem(item, levels);
            resourceSystem.onEnhance();
        }
    }

    public String getCharacterReport(String id) {
        Character c = inventory.getCharacter(id);
        return c != null ? c.generateReport() : "Character not found";
    }
}
