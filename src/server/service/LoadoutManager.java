package server.service;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.items.Artifact;
import server.model.items.Weapon;
import server.model.loadout.Loadout;
import server.model.player.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadoutManager {
    private final Inventory inventory;
    private final Map<String, Loadout> loadoutsByCharacterId;
    private String activeCharacterId;

    public LoadoutManager(Inventory inventory) {
        this.inventory = inventory;
        this.loadoutsByCharacterId = new LinkedHashMap<>();
    }

    public Loadout saveLoadout(Character character, Weapon weapon, Artifact artifact) {
        if (character == null) throw new IllegalArgumentException("Choose a character first.");
        Loadout loadout = loadoutsByCharacterId.get(character.getId());
        if (loadout == null) {
            loadout = new Loadout(character, weapon, artifact);
            loadoutsByCharacterId.put(character.getId(), loadout);
        } else {
            loadout.update(character, weapon, artifact);
        }
        activeCharacterId = character.getId();
        return loadout;
    }

    public void setActiveCharacter(String characterId) {
        if (characterId != null && inventory.getCharacter(characterId) != null) {
            activeCharacterId = characterId;
        }
    }

    public Loadout getLoadoutForCharacter(String characterId) {
        return loadoutsByCharacterId.get(characterId);
    }

    public Loadout getActiveLoadout() {
        if (activeCharacterId == null) return null;
        return loadoutsByCharacterId.get(activeCharacterId);
    }

    public Collection<Loadout> getAllLoadouts() {
        return loadoutsByCharacterId.values();
    }

    public List<Character> getCharactersWithLoadouts() {
        List<Character> result = new ArrayList<>();
        for (String id : loadoutsByCharacterId.keySet()) {
            Character c = inventory.getCharacter(id);
            if (c != null) result.add(c);
        }
        return result;
    }

    public Character getActiveCharacter() {
        if (activeCharacterId != null) {
            Character c = inventory.getCharacter(activeCharacterId);
            if (c != null) return c;
        }
        if (!loadoutsByCharacterId.isEmpty()) {
            String first = loadoutsByCharacterId.keySet().iterator().next();
            activeCharacterId = first;
            return inventory.getCharacter(first);
        }
        return null;
    }

    public Weapon getWeaponFor(Character character) {
        if (character == null) return null;
        Loadout loadout = loadoutsByCharacterId.get(character.getId());
        if (loadout == null || loadout.getWeaponId() == null) return null;
        InventoryItem item = inventory.getItem(loadout.getWeaponId());
        return item instanceof Weapon w ? w : null;
    }

    public Artifact getArtifactFor(Character character) {
        if (character == null) return null;
        Loadout loadout = loadoutsByCharacterId.get(character.getId());
        if (loadout == null || loadout.getArtifactId() == null) return null;
        InventoryItem item = inventory.getItem(loadout.getArtifactId());
        return item instanceof Artifact a ? a : null;
    }

    public boolean hasAnyLoadout() {
        return !loadoutsByCharacterId.isEmpty();
    }
}
