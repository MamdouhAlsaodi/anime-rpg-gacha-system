package server.model.player;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.exception.InventoryFullException;
import server.exception.DuplicateCharacterException;

import java.util.*;

public class Inventory {
    private Map<String, Character> characters;
    private Map<String, InventoryItem> items;
    private int capacity;

    public Inventory() {
        this.characters = new LinkedHashMap<>();
        this.items = new LinkedHashMap<>();
        this.capacity = 200;
    }

    public void addCharacter(Character c) throws InventoryFullException, DuplicateCharacterException {
        if (isFull()) throw new InventoryFullException(capacity);
        for (Character existing : characters.values()) {
            if (existing.getName().equals(c.getName())) {
                throw new DuplicateCharacterException(c.getName());
            }
        }
        characters.put(c.getId(), c);
    }

    public void addItem(InventoryItem item) throws InventoryFullException {
        if (isFull()) throw new InventoryFullException(capacity);
        items.put(item.getId(), item);
    }

    public void removeCharacter(String id) { characters.remove(id); }
    public void removeItem(String id) { items.remove(id); }
    public Character getCharacter(String id) { return characters.get(id); }
    public InventoryItem getItem(String id) { return items.get(id); }

    public Collection<Character> getAllCharacters() { return Collections.unmodifiableCollection(characters.values()); }
    public Collection<InventoryItem> getAllItems() { return Collections.unmodifiableCollection(items.values()); }

    public boolean isFull() {
        return characters.size() + items.size() >= capacity;
    }

    public int getCapacity() { return capacity; }
    public int getCharacterCount() { return characters.size(); }
    public int getItemCount() { return items.size(); }
}
