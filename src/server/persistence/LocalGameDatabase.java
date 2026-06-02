package server.persistence;

import server.model.player.Player;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple file-based database for player persistence using Java Serializable.
 * Stores players under data/players/<safe-player-id>.dat
 */
public class LocalGameDatabase {

    private final Path databaseDirectory;
    private final Path playersDirectory;

    public LocalGameDatabase() {
        this.databaseDirectory = Paths.get("data");
        this.playersDirectory = databaseDirectory.resolve("players");
    }

    public LocalGameDatabase(Path dbPath) {
        this.databaseDirectory = dbPath;
        this.playersDirectory = dbPath.resolve("players");
    }

    /**
     * Initializes the database directory structure.
     */
    public void initialize() throws IOException {
        if (!Files.exists(databaseDirectory)) {
            Files.createDirectories(databaseDirectory);
        }
        if (!Files.exists(playersDirectory)) {
            Files.createDirectories(playersDirectory);
        }
    }

    /**
     * Gets the database directory path.
     */
    public Path getDatabaseDirectory() {
        return databaseDirectory;
    }

    /**
     * Generates a safe filename for a player ID.
     */
    private String toSafeFilename(String playerId) {
        return playerId.replaceAll("[^a-zA-Z0-9_-]", "_") + ".dat";
    }

    /**
     * Gets the file path for a player.
     */
    private Path getPlayerFile(String playerId) {
        return playersDirectory.resolve(toSafeFilename(playerId));
    }

    /**
     * Saves a player to disk.
     */
    public void savePlayer(Player player) throws IOException {
        initialize();
        Path playerFile = getPlayerFile(player.getName());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(playerFile)))) {
            oos.writeObject(player);
        }
    }

    /**
     * Loads a player from disk.
     */
    public Optional<Player> loadPlayer(String playerId) throws IOException, ClassNotFoundException {
        Path playerFile = getPlayerFile(playerId);
        if (!Files.exists(playerFile)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(playerFile)))) {
            Player player = (Player) ois.readObject();
            return Optional.of(player);
        }
    }

    /**
     * Deletes a player from disk.
     */
    public boolean deletePlayer(String playerId) throws IOException {
        Path playerFile = getPlayerFile(playerId);
        if (Files.exists(playerFile)) {
            Files.delete(playerFile);
            return true;
        }
        return false;
    }

    /**
     * Lists all player IDs in the database.
     */
    public List<String> listPlayerIds() throws IOException {
        if (!Files.exists(playersDirectory)) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>();
        try (var stream = Files.list(playersDirectory)) {
            stream.forEach(path -> {
                String filename = path.getFileName().toString();
                if (filename.endsWith(".dat")) {
                    String id = filename.substring(0, filename.length() - 4);
                    ids.add(id);
                }
            });
        }
        return ids;
    }
}
