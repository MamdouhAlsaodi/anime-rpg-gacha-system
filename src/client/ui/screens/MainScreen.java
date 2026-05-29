package client.ui.screens;

import client.network.ServerConnector;
import client.ui.components.GemBar;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {
    private ServerConnector connector;
    private GemBar gemBar;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainScreen(ServerConnector connector) {
        this.connector = connector;
        String playerName = connector.isOfflineMode() ?
            connector.getOfflineEngine().getPlayer().getName() : "Player";
        int playerGems = connector.isOfflineMode() ?
            connector.getOfflineEngine().getPlayer().getGems() : 10000;

        setTitle("Anime RPG Gacha System - " + playerName);
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(10, 10, 15));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(20, 20, 30));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(201, 168, 76)));

        JLabel titleLabel = new JLabel("  " + playerName + " | Anime RPG Gacha");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(201, 168, 76));
        topBar.add(titleLabel, BorderLayout.WEST);

        gemBar = new GemBar(playerGems);
        topBar.add(gemBar, BorderLayout.EAST);

        // Navigation tabs
        JPanel navBar = new JPanel(new GridLayout(1, 4));
        navBar.setBackground(new Color(15, 15, 25));
        String[] tabs = {"Summon", "Inventory", "Player Stats", "Presentation"};
        for (String tab : tabs) {
            JButton btn = new JButton(tab);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBackground(new Color(30, 30, 45));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> cardLayout.show(cardPanel, tab));
            navBar.add(btn);
        }

        // Card panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(10, 10, 15));

        Runnable refreshGems = () -> {
            if (connector.isOfflineMode() && connector.getOfflineEngine() != null) {
                gemBar.updateGems(connector.getOfflineEngine().getPlayer().getGems());
            }
        };

        cardPanel.add(new SummonScreen(connector, refreshGems), "Summon");
        cardPanel.add(new InventoryScreen(connector), "Inventory");

        // Player stats panel
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBackground(new Color(10, 10, 15));
        JLabel playerLabel = new JLabel("Player Stats - " + playerName, SwingConstants.CENTER);
        playerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        playerLabel.setForeground(new Color(201, 168, 76));
        playerPanel.add(playerLabel, BorderLayout.NORTH);

        JTextArea statsArea = new JTextArea();
        statsArea.setBackground(new Color(20, 20, 30));
        statsArea.setForeground(Color.WHITE);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsArea.setEditable(false);
        if (connector.isOfflineMode()) {
            statsArea.setText(connector.getOfflineEngine().getStats());
        }
        playerPanel.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        JButton refreshStats = new JButton("Refresh Stats");
        refreshStats.setBackground(new Color(201, 168, 76));
        refreshStats.setForeground(Color.BLACK);
        refreshStats.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshStats.addActionListener(e -> {
            if (connector.isOfflineMode()) {
                statsArea.setText(connector.getOfflineEngine().getStats());
            }
        });
        playerPanel.add(refreshStats, BorderLayout.SOUTH);
        cardPanel.add(playerPanel, "Player Stats");

        cardPanel.add(new PresentationScreen(), "Presentation");

        // Layout
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(navBar, BorderLayout.BEFORE_FIRST_LINE);
        add(cardPanel, BorderLayout.CENTER);

        // Status bar
        JLabel status = new JLabel(" " + (connector.isOfflineMode() ? "Offline Mode | " + playerName : "Connected to server"));
        status.setForeground(new Color(136, 136, 136));
        status.setBackground(new Color(10, 10, 15));
        add(status, BorderLayout.SOUTH);
    }

    public GemBar getGemBar() { return gemBar; }
}