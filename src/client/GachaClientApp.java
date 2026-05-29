package client;

import client.ui.screens.MainScreen;

import javax.swing.*;

public class GachaClientApp {
    public static void main(String[] args) {
        System.out.println("Starting Anime RPG Gacha Client...");

        String username = JOptionPane.showInputDialog(null,
            "<html><h2 style='color:#C9A84C'>Welcome to Anime RPG Gacha!</h2>" +
            "<p>Enter your username:</p></html>",
            "Anime RPG Gacha", JOptionPane.QUESTION_MESSAGE);

        if (username == null || username.trim().isEmpty()) {
            username = "Player";
        }
        final String finalUsername = username.trim();

        SwingUtilities.invokeLater(() -> new MainScreen(finalUsername));
    }
}