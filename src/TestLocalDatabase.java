import server.model.player.Player;
import server.persistence.LocalGameDatabase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Simple test for LocalGameDatabase.
 * Covers creating, saving, loading, modifying, listing, and deleting player data.
 */
public class TestLocalDatabase {
    private static final String TEST_PLAYER = "test_player_12345";
    private static boolean allPassed = true;

    public static void main(String[] args) {
        System.out.println("=== LocalGameDatabase Test ===\n");
        LocalGameDatabase db = new LocalGameDatabase(Paths.get("data-test"));

        try {
            db.deletePlayer(TEST_PLAYER);
        } catch (IOException ignored) {
        }

        testCreateAndSave(db);
        testLoad(db);
        testModifyAndResave(db);
        testVerifyModified(db);
        testListPlayerIds(db);
        testDeletePlayer(db);
        testVerifyDeletion(db);

        System.out.println("\n=== Test Result ===");
        if (allPassed) {
            System.out.println("PASS: All tests passed!");
        } else {
            System.out.println("FAIL: Some tests failed.");
            System.exit(1);
        }
    }

    private static void testCreateAndSave(LocalGameDatabase db) {
        try {
            Player player = new Player(TEST_PLAYER, 1600);
            assertEquals("Starting gems", 1600, player.getGems());
            assertEquals("Starting pulls", 0, player.getTotalPulls());
            assertEquals("Starting pity", 0, player.getPityCounter());
            db.savePlayer(player);
            pass("Create and save player");
        } catch (Exception e) {
            fail("Create and save player", e);
        }
    }

    private static void testLoad(LocalGameDatabase db) {
        try {
            Player loaded = db.loadPlayer(TEST_PLAYER).orElseThrow();
            assertEquals("Player name", TEST_PLAYER, loaded.getName());
            assertEquals("Player gems", 1600, loaded.getGems());
            pass("Load player");
        } catch (Exception e) {
            fail("Load player", e);
        }
    }

    private static void testModifyAndResave(LocalGameDatabase db) {
        try {
            Player player = db.loadPlayer(TEST_PLAYER).orElseThrow();
            player.addGems(500);
            player.spendGems(160);
            player.incrementPity();
            assertEquals("Gems after modification", 1940, player.getGems());
            assertEquals("Pulls after spend", 1, player.getTotalPulls());
            assertEquals("Pity after increment", 1, player.getPityCounter());
            db.savePlayer(player);
            pass("Modify and re-save player");
        } catch (Exception e) {
            fail("Modify and re-save player", e);
        }
    }

    private static void testVerifyModified(LocalGameDatabase db) {
        try {
            Player loaded = db.loadPlayer(TEST_PLAYER).orElseThrow();
            assertEquals("Reloaded gems", 1940, loaded.getGems());
            assertEquals("Reloaded pulls", 1, loaded.getTotalPulls());
            assertEquals("Reloaded pity", 1, loaded.getPityCounter());
            pass("Verify modified values persisted");
        } catch (Exception e) {
            fail("Verify modified values persisted", e);
        }
    }

    private static void testListPlayerIds(LocalGameDatabase db) {
        try {
            List<String> ids = db.listPlayerIds();
            if (!ids.contains(TEST_PLAYER)) {
                throw new AssertionError("Test player ID not in list: " + ids);
            }
            pass("List player IDs");
        } catch (Exception e) {
            fail("List player IDs", e);
        }
    }

    private static void testDeletePlayer(LocalGameDatabase db) {
        try {
            boolean deleted = db.deletePlayer(TEST_PLAYER);
            if (!deleted) throw new AssertionError("deletePlayer returned false");
            pass("Delete player");
        } catch (Exception e) {
            fail("Delete player", e);
        }
    }

    private static void testVerifyDeletion(LocalGameDatabase db) {
        try {
            if (db.loadPlayer(TEST_PLAYER).isPresent()) {
                throw new AssertionError("Player still exists after deletion");
            }
            pass("Verify player deleted");
        } catch (Exception e) {
            fail("Verify player deleted", e);
        }
    }

    private static void assertEquals(String description, int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(String description, String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(description + ": expected '" + expected + "', got '" + actual + "'");
        }
    }

    private static void pass(String testName) {
        System.out.println("PASS: " + testName);
    }

    private static void fail(String testName, Exception e) {
        System.out.println("FAIL: " + testName + " - " + e.getMessage());
        e.printStackTrace();
        allPassed = false;
    }
}
