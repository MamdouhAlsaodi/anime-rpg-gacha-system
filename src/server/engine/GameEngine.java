package server.engine;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.characters.Hero;
import server.model.player.Player;
import server.model.player.Inventory;
import server.service.*;
import server.factory.CharacterFactory;
import server.factory.WeaponFactory;
import server.factory.ArtifactFactory;
import server.exception.InsufficientGemsException;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private Player player;
    private Inventory inventory;
    private GachaSystem gachaSystem;
    private InventoryManager inventoryManager;
    private ConstellationSystem constellationSystem;
    private ProgressionSystem progressionSystem;
    private CharacterFactory characterFactory;
    private WeaponFactory weaponFactory;
    private ArtifactFactory artifactFactory;

    public GameEngine() {
        this.player = new Player("Player", 10000);
        this.inventory = new Inventory();
        this.gachaSystem = new GachaSystem();
        this.inventoryManager = new InventoryManager();
        this.constellationSystem = new ConstellationSystem();
        this.progressionSystem = new ProgressionSystem();
        this.characterFactory = new CharacterFactory();
        this.weaponFactory = new WeaponFactory();
        this.artifactFactory = new ArtifactFactory();
    }

    public List<Object> performSummon(int count) throws InsufficientGemsException {
        if (count == 1) {
            List<Object> result = new ArrayList<>();
            Object summoned = gachaSystem.summonSingle(player, characterFactory, weaponFactory, artifactFactory);
            result.add(summoned);
            addToInventory(summoned);
            return result;
        } else {
            List<Object> results = gachaSystem.summonTen(player, characterFactory, weaponFactory, artifactFactory);
            for (Object obj : results) {
                addToInventory(obj);
            }
            return results;
        }
    }

    private void addToInventory(Object obj) {
        if (obj instanceof Character c) {
            inventoryManager.addCharacterToInventory(inventory, c);
        } else if (obj instanceof InventoryItem item) {
            inventoryManager.addItemToInventory(inventory, item);
        }
    }

    public Inventory getInventory() { return inventory; }
    public Player getPlayer() { return player; }
    public GachaSystem getGachaSystem() { return gachaSystem; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public ConstellationSystem getConstellationSystem() { return constellationSystem; }
    public ProgressionSystem getProgressionSystem() { return progressionSystem; }

    public void levelUpCharacter(String id, int levels) {
        Character c = inventory.getCharacter(id);
        if (c != null) {
            progressionSystem.levelUp(c, levels);
        }
    }

    public void enhanceItem(String id, int levels) {
        InventoryItem item = inventory.getItem(id);
        if (item != null) {
            progressionSystem.enhanceItem(item, levels);
        }
    }

    public String getCharacterReport(String id) {
        Character c = inventory.getCharacter(id);
        return c != null ? c.generateReport() : "Character not found";
    }
}
