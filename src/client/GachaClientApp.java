package client;

import client.network.ServerConnector;
import client.ui.screens.MainScreen;

import javax.swing.*;

public class GachaClientApp {
    public static void main(String[] args) {
        System.out.println("Starting Anime RPG Gacha Client...");

        // Ask for username
        String username = JOptionPane.showInputDialog(null,
            "<html><h2 style='color:#C9A84C'>Welcome to Anime RPG Gacha!</h2>" +
            "<p>Enter your username:</p></html>",
            "Anime RPG Gacha - Login",
            JOptionPane.PLAIN_MESSAGE);

        if (username == null || username.trim().isEmpty()) {
            username = "Player";
        }
        username = username.trim();

        // Check for VIP
        int startingGems = 10000;
        if (username.equalsIgnoreCase("mamdouh")) {
            startingGems = 999999;
            JOptionPane.showMessageDialog(null,
                "<html><h2 style='color:#FFD700'>*** VIP ACCESS GRANTED ***</h2>" +
                "<p style='color:#C9A84C'>Welcome back, Master Mamdouh!</p>" +
                "<p style='color:#4FC3F7'>Bonus: 999,999 Gems!</p></html>",
                "VIP Login",
                JOptionPane.INFORMATION_MESSAGE);
        }

        final String playerName = username;
        final int gems = startingGems;

        ServerConnector connector = new ServerConnector();
        connector.connect("localhost", 8080);

        // Set player info in offline mode
        if (connector.isOfflineMode()) {
            connector.getOfflineEngine().getPlayer().setName(playerName);
            connector.getOfflineEngine().getPlayer().setGems(gems);
        }

        SwingUtilities.invokeLater(() -> {
            MainScreen screen = new MainScreen(connector);
            screen.setVisible(true);
        });
    }
}