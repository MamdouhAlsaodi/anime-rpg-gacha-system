package client;

import client.network.ServerConnector;
import client.ui.screens.MainScreen;

import javax.swing.*;

public class GachaClientApp {
    public static void main(String[] args) {
        System.out.println("Starting Anime RPG Gacha Client...");

        ServerConnector connector = new ServerConnector();
        connector.connect("localhost", 8080);

        SwingUtilities.invokeLater(() -> {
            MainScreen screen = new MainScreen(connector);
            screen.setVisible(true);
        });
    }
}
