package client;

import client.ui.screens.MainScreen;
import client.ui.screens.UserSelectionDialog;

import javax.swing.*;

public class GachaClientApp {
    public static void main(String[] args) {
        System.out.println("Starting Anime RPG Gacha Client...");

        SwingUtilities.invokeLater(() -> {
            String username = UserSelectionDialog.chooseUser();
            if (username == null || username.trim().isEmpty()) {
                System.out.println("No user selected. Exiting.");
                System.exit(0);
                return;
            }
            new MainScreen(username.trim());
        });
    }
}
