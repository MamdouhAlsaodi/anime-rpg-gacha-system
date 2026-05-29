import server.engine.GameEngine;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.characters.Hero;
import server.model.enums.Rarity;
import server.exception.InsufficientGemsException;

import java.util.List;

public class Main {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String GOLD = "\u001B[33;1m";

    private static int totalLegendaries = 0;
    private static int totalRares = 0;
    private static int totalCommons = 0;

    public static void main(String[] args) {
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println(GOLD + "  ANIME RPG GACHA SYSTEM - STRESS TEST" + RESET);
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println();

        scenario1();
        System.out.println();
        scenario2();
        System.out.println();
        scenario3();
        System.out.println();
        scenario4();
        System.out.println();

        printSummary();
    }

    static void scenario1() {
        System.out.println(CYAN + "=== SCENARIO 1: Basic Summon ===" + RESET);
        GameEngine engine = new GameEngine();
        try {
            List<Object> results = engine.performSummon(1);
            for (Object obj : results) {
                printSummonResult(obj);
            }
            System.out.println(GREEN + "Player: " + engine.getPlayer() + RESET);
        } catch (InsufficientGemsException e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    static void scenario2() {
        System.out.println(CYAN + "=== SCENARIO 2: Multi Summon (x10) ===" + RESET);
        GameEngine engine = new GameEngine();
        try {
            List<Object> results = engine.performSummon(10);
            System.out.println("Summoned " + results.size() + " items:");
            for (int i = 0; i < results.size(); i++) {
                System.out.print("  " + (i+1) + ". ");
                printSummonResult(results.get(i));
            }
            System.out.println(GREEN + "Pity: " + engine.getPlayer().getPityCounter() + RESET);
            System.out.println(GREEN + "Soft Pity: " + engine.getPlayer().getSoftPityCounter() + RESET);
        } catch (InsufficientGemsException e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    static void scenario3() {
        System.out.println(CYAN + "=== SCENARIO 3: Inventory Full ===" + RESET);
        GameEngine engine = new GameEngine();
        try {
            System.out.println("Filling inventory...");
            for (int i = 0; i < 25; i++) {
                engine.performSummon(10);
            }
            System.out.println(GREEN + "Inventory: " + engine.getInventory().getCharacterCount() +
                " chars, " + engine.getInventory().getItemCount() + " items" + RESET);
            engine.getPlayer().addGems(100000);
            for (int i = 0; i < 10; i++) {
                engine.performSummon(10);
            }
            System.out.println(GREEN + "After more: " +
                (engine.getInventory().getCharacterCount() + engine.getInventory().getItemCount()) +
                " / " + engine.getInventory().getCapacity() + RESET);
        } catch (Exception e) {
            System.out.println(YELLOW + "Caught: " + e.getMessage() + RESET);
        }
    }

    static void scenario4() {
        System.out.println(CYAN + "=== SCENARIO 4: Constellation System ===" + RESET);
        GameEngine engine = new GameEngine();
        engine.getPlayer().addGems(500000);
        try {
            System.out.println("50 summons looking for duplicates...");
            for (int i = 0; i < 50; i++) {
                engine.performSummon(10);
            }
            int found = 0;
            for (Character c : engine.getInventory().getAllCharacters()) {
                if (c instanceof Hero hero && hero.getConstellation() > 0) {
                    System.out.println(GOLD + "  " + hero.getName() + " [C" + hero.getConstellation() + "] " + hero.getRarity() + RESET);
                    found++;
                }
            }
            if (found == 0) {
                System.out.println(YELLOW + "  No constellation upgrades yet" + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    static void printSummonResult(Object obj) {
        if (obj instanceof Character c) {
            String color = c.getRarity() == Rarity.LEGENDARY ? GOLD : c.getRarity() == Rarity.RARE ? BLUE : RESET;
            if (c.getRarity() == Rarity.LEGENDARY) totalLegendaries++;
            else if (c.getRarity() == Rarity.RARE) totalRares++;
            else totalCommons++;
            System.out.println(color + "[CHAR] " + c.getName() + " [" + c.getRarity() + "] Lv." + c.getLevel() + " " + c.getElement().getDisplayName() + RESET);
        } else if (obj instanceof InventoryItem item) {
            String color = item.getRarity() == Rarity.LEGENDARY ? GOLD : item.getRarity() == Rarity.RARE ? BLUE : RESET;
            if (item.getRarity() == Rarity.LEGENDARY) totalLegendaries++;
            else if (item.getRarity() == Rarity.RARE) totalRares++;
            else totalCommons++;
            System.out.println(color + "[ITEM] " + item.getName() + " [" + item.getRarity() + "] " + item.getItemType() + RESET);
        }
    }

    static void printSummary() {
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println(GOLD + "  SUMMARY" + RESET);
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println(GOLD + "  Legendaries: " + totalLegendaries + RESET);
        System.out.println(BLUE + "  Rares: " + totalRares + RESET);
        System.out.println("  Commons: " + totalCommons);
        System.out.println(GOLD + "  Total: " + (totalLegendaries + totalRares + totalCommons) + RESET);
    }
}
