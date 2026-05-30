package client.ui.screens;

import client.network.ServerConnector;
import client.ui.GamePanel;
import client.ui.components.GemBar;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;
import server.model.items.Artifact;
import server.model.items.Weapon;

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
        var engine = connector.getOfflineEngine();
        List<Character> chars = new ArrayList<>(engine.getInventory().getAllCharacters());
        List<InventoryItem> items = new ArrayList<>(engine.getInventory().getAllItems());

        if (chars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No characters! Summon first!");
            return;
        }

        Character selectedChar = engine.getLoadoutManager().getActiveCharacter();
        if (selectedChar == null) {
            selectedChar = chooseCharacter(chars);
            if (selectedChar == null) return;
            engine.getLoadoutManager().setActiveCharacter(selectedChar.getId());
        }

        Weapon selectedWeapon = engine.getLoadoutManager().getWeaponFor(selectedChar);
        Artifact selectedArtifact = engine.getLoadoutManager().getArtifactFor(selectedChar);

        if (selectedWeapon == null && selectedArtifact == null) {
            int choice = JOptionPane.showConfirmDialog(this,
                selectedChar.getName() + " has no saved weapon/artifact yet. Choose and save now?",
                "No Saved Loadout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                LoadoutChoice loadout = chooseAndSaveLoadout(selectedChar, items);
                if (loadout == null) return;
                selectedWeapon = loadout.weapon;
                selectedArtifact = loadout.artifact;
            }
        }

        launchAdventure(selectedChar, selectedWeapon, selectedArtifact);
    }

    private Character chooseCharacter(List<Character> chars) {
        String[] charNames = chars.stream().map(c ->
            c.getName() + " [" + c.getRarity() + "] Lv" + c.getLevel()
        ).toArray(String[]::new);
        JComboBox<String> charBox = new JComboBox<>(charNames);
        int result = JOptionPane.showConfirmDialog(this, charBox,
            "Choose Character", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION ? chars.get(charBox.getSelectedIndex()) : null;
    }

    private LoadoutChoice chooseAndSaveLoadout(Character selectedChar, List<InventoryItem> items) {
        List<Weapon> weapons = new ArrayList<>();
        List<Artifact> artifacts = new ArrayList<>();
        for (InventoryItem item : items) {
            if (item instanceof Weapon w) weapons.add(w);
            if (item instanceof Artifact a) artifacts.add(a);
        }

        JComboBox<String> weaponBox = new JComboBox<>();
        weaponBox.addItem("No Weapon");
        for (Weapon w : weapons) weaponBox.addItem(w.getName() + " [" + w.getRarity() + "] Lv" + w.getLevel());

        JComboBox<String> artifactBox = new JComboBox<>();
        artifactBox.addItem("No Artifact");
        for (Artifact a : artifacts) artifactBox.addItem(a.getName() + " [" + a.getRarity() + "] Lv" + a.getLevel());

        JPanel selPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        selPanel.setBackground(new Color(20, 20, 30));
        JLabel wl = new JLabel("Weapon for " + selectedChar.getName() + ":"); wl.setForeground(Color.WHITE);
        JLabel al = new JLabel("Artifact for " + selectedChar.getName() + ":"); al.setForeground(Color.WHITE);
        selPanel.add(wl); selPanel.add(weaponBox);
        selPanel.add(al); selPanel.add(artifactBox);

        int result = JOptionPane.showConfirmDialog(this, selPanel,
            "Save Loadout", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;

        Weapon weapon = weaponBox.getSelectedIndex() > 0 ? weapons.get(weaponBox.getSelectedIndex() - 1) : null;
        Artifact artifact = artifactBox.getSelectedIndex() > 0 ? artifacts.get(artifactBox.getSelectedIndex() - 1) : null;
        connector.getOfflineEngine().getLoadoutManager().saveLoadout(selectedChar, weapon, artifact);
        return new LoadoutChoice(weapon, artifact);
    }

    private void launchAdventure(Character selectedChar, Weapon selectedWeapon, Artifact selectedArtifact) {
        JFrame gameFrame = new JFrame("Anime RPG - 2D Adventure | " + selectedChar.getName());
        gameFrame.setSize(820, 540);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setLocationRelativeTo(this);

        GamePanel gp = new GamePanel(gameFrame, selectedChar, selectedWeapon, selectedArtifact);
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
        gp.requestFocusInWindow();
    }

    private static class LoadoutChoice {
        final Weapon weapon;
        final Artifact artifact;
        LoadoutChoice(Weapon weapon, Artifact artifact) {
            this.weapon = weapon;
            this.artifact = artifact;
        }
    }

    public GemBar getGemBar() { return gemBar; }
}