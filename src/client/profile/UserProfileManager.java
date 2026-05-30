package client.profile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UserProfileManager {
    private final Path profileDir;
    private final Path usersFile;

    public UserProfileManager() {
        this(Path.of(System.getProperty("user.home"), ".anime-rpg-gacha"));
    }

    public UserProfileManager(Path profileDir) {
        this.profileDir = profileDir;
        this.usersFile = profileDir.resolve("users.txt");
        ensureDefaults();
    }

    private void ensureDefaults() {
        try {
            Files.createDirectories(profileDir);
            if (!Files.exists(usersFile) || Files.size(usersFile) == 0) {
                writeUsers(List.of("mamdouh", "Player2"));
                return;
            }
            List<String> users = readUsers();
            boolean changed = false;
            if (users.stream().noneMatch(u -> u.equalsIgnoreCase("mamdouh"))) {
                users.add(0, "mamdouh");
                changed = true;
            }
            if (users.stream().noneMatch(u -> u.equalsIgnoreCase("Player2"))) {
                users.add("Player2");
                changed = true;
            }
            if (changed) writeUsers(users);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize user profiles: " + e.getMessage(), e);
        }
    }

    public List<String> readUsers() {
        try {
            if (!Files.exists(usersFile)) return new ArrayList<>();
            Set<String> unique = new LinkedHashSet<>();
            for (String line : Files.readAllLines(usersFile, StandardCharsets.UTF_8)) {
                String cleaned = sanitize(line);
                if (!cleaned.isEmpty() && unique.stream().noneMatch(u -> u.equalsIgnoreCase(cleaned))) {
                    unique.add(cleaned);
                }
            }
            return new ArrayList<>(unique);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read user profiles: " + e.getMessage(), e);
        }
    }

    public String addUser(String rawName) {
        String username = sanitize(rawName);
        validateNewUsername(username);
        List<String> users = readUsers();
        users.add(username);
        writeUsers(users);
        return username;
    }

    public boolean userExists(String rawName) {
        String username = sanitize(rawName);
        return readUsers().stream().anyMatch(u -> u.equalsIgnoreCase(username));
    }

    public String sanitize(String rawName) {
        if (rawName == null) return "";
        return rawName.trim().replaceAll("\\s+", " ");
    }

    public void validateNewUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (username.length() > 24) {
            throw new IllegalArgumentException("Username must be 24 characters or less.");
        }
        if (!username.matches("[A-Za-z0-9_\\- \\p{IsArabic}]+")) {
            throw new IllegalArgumentException("Username can use letters, numbers, spaces, underscore, or dash only.");
        }
        if (userExists(username)) {
            throw new IllegalArgumentException("This user already exists.");
        }
    }

    private void writeUsers(List<String> users) {
        try {
            Files.createDirectories(profileDir);
            Set<String> unique = new LinkedHashSet<>();
            for (String user : users) {
                String cleaned = sanitize(user);
                if (!cleaned.isEmpty() && unique.stream().noneMatch(u -> u.equalsIgnoreCase(cleaned))) {
                    unique.add(cleaned);
                }
            }
            Files.write(usersFile, unique, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save user profiles: " + e.getMessage(), e);
        }
    }

    public Path getUsersFile() { return usersFile; }
}
