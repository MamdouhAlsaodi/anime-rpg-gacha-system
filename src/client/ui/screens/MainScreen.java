package client.ui.screens;

import client.network.ServerConnector;
import client.ui.GamePanel;
import client.ui.components.GemBar;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class MainScreen extends JFrame {
    private ServerConnector connector;
    private GemBar gemBar;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private String playerName;

    public MainScreen(String username) {
        this.connector = new ServerConnector(username);
        this.playerName = username;
        initUI();
    }

    private void initUI() {
        int playerGems = connector.isOfflineMode() ?
            connector.getOfflineEngine().getPlayer().getGems() : 10000;

        setTitle("Anime RPG Gacha - " + playerName);
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

        // Navigation tabs - full-width equal tabs
        String[] tabs = {"Summon", "Inventory", "Missions", "Upgrade", "Play Game", "Presentation"};
        JPanel navBar = new JPanel(new GridLayout(1, tabs.length));
        navBar.setBackground(new Color(15, 15, 25));
        for (String tab : tabs) {
            JButton btn = new JButton(tab);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBackground(tab.equals("Play Game") ? new Color(180, 50, 50) : new Color(30, 30, 45));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (tab.equals("Play Game")) {
                    openGameWindow();
                } else {
                    cardLayout.show(cardPanel, tab);
                }
            });
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
        cardPanel.add(new MissionsScreen(connector, refreshGems), "Missions");
        cardPanel.add(new UpgradeScreen(connector, refreshGems), "Upgrade");

        // Game placeholder card
        JPanel gameCard = new JPanel(new BorderLayout());
        gameCard.setBackground(new Color(10, 10, 15));
        JLabel gameLabel = new JLabel("<html><center><h1 style='color:#C9A84C'>2D Adventure Mode</h1>" +
            "<p style='color:white;font-size:16px'>Click 'Play Game' tab to start!</p>" +
            "<p style='color:gray'>Use your gacha characters and weapons in battle!</p></center></html>", SwingConstants.CENTER);
        gameCard.add(gameLabel, BorderLayout.CENTER);
        cardPanel.add(gameCard, "Play Game");

        cardPanel.add(new PresentationScreen(), "Presentation");

        // Layout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 15, 25));
        header.add(topBar, BorderLayout.NORTH);
        header.add(navBar, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        JLabel status = new JLabel(" " + (connector.isOfflineMode() ? "Offline Mode | " + playerName : "Connected to server"));
        status.setForeground(new Color(136, 136, 136));
        status.setBackground(new Color(10, 10, 15));
        status.setOpaque(true);
        add(status, BorderLayout.SOUTH);

        revalidate();
        repaint();
        setVisible(true);
    }

    private void openGameWindow() {
        // Pick character
        List<Character> chars = new ArrayList<>(connector.getOfflineEngine().getInventory().getAllCharacters());
        List<InventoryItem> items = new ArrayList<>(connector.getOfflineEngine().getInventory().getAllItems());

        if (chars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No characters! Summon first!");
            return;
        }

        // Character selection
        String[] charNames = chars.stream().map(c ->
            c.getName() + " [" + c.getRarity() + "] Lv" + c.getLevel()
        ).toArray(String[]::new);

        JComboBox<String> charBox = new JComboBox<>(charNames);

        // Weapon selection
        String[] weaponNames = new String[items.size() + 1];
        weaponNames[0] = "No Weapon (Fists)";
        for (int i = 0; i < items.size(); i++) {
            weaponNames[i + 1] = items.get(i).getName() + " [" + items.get(i).getRarity() + "]";
        }
        JComboBox<String> weaponBox = new JComboBox<>(weaponNames);

        JPanel selPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        selPanel.setBackground(new Color(20, 20, 30));
        JLabel cl = new JLabel("Choose Character:"); cl.setForeground(Color.WHITE);
        JLabel wl = new JLabel("Choose Weapon:"); wl.setForeground(Color.WHITE);
        selPanel.add(cl); selPanel.add(charBox);
        selPanel.add(wl); selPanel.add(weaponBox);

        int result = JOptionPane.showConfirmDialog(this, selPanel,
            "Select Your Loadout", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        Character selectedChar = chars.get(charBox.getSelectedIndex());
        InventoryItem selectedWeapon = weaponBox.getSelectedIndex() > 0 ?
            items.get(weaponBox.getSelectedIndex() - 1) : null;

        // Open game window
        JFrame gameFrame = new JFrame("Anime RPG - 2D Adventure");
        gameFrame.setSize(820, 540);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setLocationRelativeTo(this);

        GamePanel gp = new GamePanel(gameFrame, selectedChar, selectedWeapon);
        gameFrame.add(gp);
        gameFrame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                int earned = gp.getTotalGemsEarned();
                if (earned > 0 && connector.isOfflineMode()) {
                    connector.getOfflineEngine().getPlayer().addGems(earned);
                    gemBar.updateGems(connector.getOfflineEngine().getPlayer().getGems());
                    JOptionPane.showMessageDialog(MainScreen.this,
                        "Adventure reward added: +" + earned + " Gems!");
                }
            }
        });
        gameFrame.setVisible(true);

        // Show the Stage 1 teaser first. The player starts by pressing ENTER.
        gp.requestFocusInWindow();
    }

    public GemBar getGemBar() { return gemBar; }
}