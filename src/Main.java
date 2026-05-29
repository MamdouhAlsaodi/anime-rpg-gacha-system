import server.engine.GameEngine;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.characters.Hero;
import server.model.enums.Rarity;
import server.exception.InsufficientGemsException;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String GOLD = "\u001B[33;1m";
    private static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println(GOLD + "  ANIME RPG GACHA SYSTEM v1.0" + RESET);
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println();
        System.out.print(CYAN + "Enter your username: " + RESET);
        String username = scanner.nextLine().trim();

        int startingGems = 10000;
        if (username.equalsIgnoreCase("mamdouh")) {
            startingGems = 999999;
            System.out.println();
            System.out.println(GOLD + "  *** Welcome back, Master Mamdouh! ***" + RESET);
            System.out.println(GOLD + "  *** VIP Bonus: 999,999 Gems granted! ***" + RESET);
            System.out.println(GOLD + "  *** May luck be on your side! ***" + RESET);
            System.out.println();
        } else {
            System.out.println();
            System.out.println(GREEN + "  Welcome, " + username + "! Starting with " + String.format("%,d", startingGems) + " gems." + RESET);
            System.out.println();
        }

        GameEngine engine = new GameEngine(username, startingGems);

        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println(GOLD + "  RUNNING STRESS TEST (4 Scenarios)" + RESET);
        System.out.println(GOLD + "===========================================" + RESET);
        System.out.println();

        scenario1(engine);
        System.out.println();
        scenario2(engine);
        System.out.println();
        scenario3(engine);
        System.out.println();
        scenario4(engine);
        System.out.println();

        // Final summary
        System.out.println(engine.getStats());

        // Show legendary characters if any
        System.out.println(GOLD + "======= LEGENDARY COLLECTION =======" + RESET);
        boolean found = false;
        for (Character c : engine.getInventory().getAllCharacters()) {
            if (c.getRarity() == Rarity.LEGENDARY) {
                System.out.println(GOLD + "  >> " + c.getName() + " [Lv." + c.getLevel() + "] " + c.getElement().getDisplayName() + RESET);
                found = true;
            }
        }
        if (!found) {
            System.out.println(YELLOW + "  No legendaries pulled... better luck next time!" + RESET);
        }

        // Show constellation upgrades
        System.out.println();
        System.out.println(PURPLE + "======= CONSTELLATION UPGRADES =======" + RESET);
        boolean constFound = false;
        for (Character c : engine.getInventory().getAllCharacters()) {
            if (c instanceof Hero hero && hero.getConstellation() > 0) {
                System.out.println(PURPLE + "  >> " + hero.getName() + " C" + hero.getConstellation() + " [" + hero.getRarity() + "]" + RESET);
                constFound = true;
            }
        }
        if (!constFound) {
            System.out.println(YELLOW + "  No constellation upgrades yet." + RESET);
        }

        System.out.println();
        System.out.println(GOLD + "======= FINAL STATS =======" + RESET);
        System.out.println(engine.getStats());

        scanner.close();
    }

    static void scenario1(GameEngine engine) {
        System.out.println(CYAN + "=== SCENARIO 1: Basic Summon ===" + RESET);
        try {
            List<Object> results = engine.performSummon(1);
            for (Object obj : results) {
                printSummonResult(obj);
            }
        } catch (InsufficientGemsException e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    static void scenario2(GameEngine engine) {
        System.out.println(CYAN + "=== SCENARIO 2: Multi Summon (x10) ===" + RESET);
        try {
            List<Object> results = engine.performSummon(10);
            System.out.println("Summoned " + results.size() + " items:");
            for (int i = 0; i < results.size(); i++) {
                System.out.print("  " + (i + 1) + ". ");
                printSummonResult(results.get(i));
            }
        } catch (InsufficientGemsException e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    static void scenario3(GameEngine engine) {
        System.out.println(CYAN + "=== SCENARIO 3: Inventory Stress Test ===" + RESET);
        int pullsBefore = engine.getTotalPulls();
        int gemsBefore = engine.getPlayer().getGems();
        System.out.println("Gems: " + String.format("%,d", gemsBefore));
        System.out.println("Pulling until out of gems or inventory full...");

        int round = 0;
        while (engine.getPlayer().getGems() >= 1600 && !engine.getInventory().isFull()) {
            try {
                engine.performSummon(10);
                round++;
                if (round % 10 == 0) {
                    System.out.print(GREEN + "." + RESET);
                }
            } catch (InsufficientGemsException e) {
                break;
            }
        }
        // Single pulls with remaining gems
        while (engine.getPlayer().getGems() >= 160 && !engine.getInventory().isFull()) {
            try {
                engine.performSummon(1);
            } catch (InsufficientGemsException e) {
                break;
            }
        }

        int pullsAfter = engine.getTotalPulls();
        System.out.println();
        System.out.println(GREEN + "Done! Pulled " + (pullsAfter - pullsBefore) + " times in " + round + " rounds" + RESET);
        System.out.println(GREEN + "Inventory: " + engine.getInventory().getCharacterCount() +
            " chars, " + engine.getInventory().getItemCount() + " items (" +
            (engine.getInventory().getCharacterCount() + engine.getInventory().getItemCount()) +
            "/" + engine.getInventory().getCapacity() + ")" + RESET);
        System.out.println("Gems remaining: " + String.format("%,d", engine.getPlayer().getGems()));
    }

    static void scenario4(GameEngine engine) {
        System.out.println(CYAN + "=== SCENARIO 4: Constellation Hunt ===" + RESET);
        engine.getPlayer().addGems(500000);
        System.out.println("Added 500,000 bonus gems for constellation hunting!");
        System.out.println("Pulling 100 batches of x10...");

        for (int i = 0; i < 100; i++) {
            try {
                engine.performSummon(10);
            } catch (InsufficientGemsException e) {
                System.out.println(YELLOW + "Out of gems at batch " + (i + 1) + RESET);
                break;
            }
        }

        System.out.println(GREEN + "Total pulls: " + engine.getTotalPulls() + RESET);
        System.out.println(GREEN + "Legendaries: " + engine.getLegendaryCount() + RESET);
        System.out.println(GREEN + "Constellation upgrades: " + engine.getConstellationUpgrades() + RESET);
    }

    static void printSummonResult(Object obj) {
        if (obj instanceof Character c) {
            String color = c.getRarity() == Rarity.LEGENDARY ? GOLD : c.getRarity() == Rarity.RARE ? BLUE : RESET;
            System.out.println(color + "[CHAR] " + c.getName() + " [" + c.getRarity() + "] Lv." + c.getLevel() +
                " " + c.getElement().getDisplayName() + RESET);
        } else if (obj instanceof InventoryItem item) {
            String color = item.getRarity() == Rarity.LEGENDARY ? GOLD : item.getRarity() == Rarity.RARE ? BLUE : RESET;
            System.out.println(color + "[ITEM] " + item.getName() + " [" + item.getRarity() + "] " + item.getItemType() + RESET);
        }
    }
}