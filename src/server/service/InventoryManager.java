package server.service;

import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.player.Inventory;
import server.exception.InventoryFullException;
import server.exception.DuplicateCharacterException;

public class InventoryManager {

    public String addCharacterToInventory(Inventory inv, Character c) {
        try {
            inv.addCharacter(c);
            return "Added character: " + c.getName();
        } catch (InventoryFullException e) {
            return "FAILED: " + e.getMessage();
        } catch (DuplicateCharacterException e) {
            return "DUPLICATE: " + e.getMessage();
        }
    }

    public String addItemToInventory(Inventory inv, InventoryItem item) {
        try {
            inv.addItem(item);
            return "Added item: " + item.getName();
        } catch (InventoryFullException e) {
            return "FAILED: " + e.getMessage();
        }
    }

    public void removeCharacter(Inventory inv, String id) {
        inv.removeCharacter(id);
    }

    public void removeItem(Inventory inv, String id) {
        inv.removeItem(id);
    }

    public String getInventorySummary(Inventory inv) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTORY SUMMARY ===\n");
        sb.append(String.format("Characters: %d | Items: %d | Capacity: %d/%d%n",
            inv.getCharacterCount(), inv.getItemCount(),
            inv.getCharacterCount() + inv.getItemCount(), inv.getCapacity()));
        sb.append("\n--- Characters ---\n");
        for (Character c : inv.getAllCharacters()) {
            sb.append(String.format("  %s [%s] Lv.%d %s%n", c.getName(), c.getRarity(), c.getLevel(), c.getElement().getDisplayName()));
        }
        sb.append("\n--- Items ---\n");
        for (InventoryItem item : inv.getAllItems()) {
            sb.append(String.format("  %s [%s] %s Lv.%d%n", item.getName(), item.getRarity(), item.getItemType(), item.getLevel()));
        }
        return sb.toString();
    }
}
