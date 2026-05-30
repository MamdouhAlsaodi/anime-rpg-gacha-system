package client.ui.screens;

import client.network.ServerConnector;
import client.ui.components.CharacterCard;
import client.ui.components.ItemCard;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.items.Artifact;
import server.model.items.Relic;
import server.model.items.Weapon;
import server.model.loadout.Loadout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryScreen extends JPanel {
    private final ServerConnector connector;
    private final JPanel contentPanel;
    private final JPanel loadoutSummary;
    private JComboBox<Character> characterBox;
    private JComboBox<WeaponOption> weaponBox;
    private JComboBox<ArtifactOption> artifactBox;
    private JLabel statusLabel;
    private String currentFilter = "All";

    private static final Color BG = new Color(10, 10, 15);
    private static final Color PANEL = new Color(20, 20, 30);
    private static final Color GOLD = new Color(201, 168, 76);
    private static final Color TAB = new Color(30, 30, 45);

    public InventoryScreen(ServerConnector connector) {
        this.connector = connector;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);

        contentPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        contentPanel.setBackground(BG);
        JScrollPane itemScroll = darkScroll(contentPanel);

        JPanel loadoutPanel = buildLoadoutPanel();
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, itemScroll, loadoutPanel);
        split.setResizeWeight(0.68);
        split.setDividerLocation(390);
        split.setBorder(null);
        split.setBackground(BG);
        add(split, BorderLayout.CENTER);

        statusLabel = new JLabel(" Summon first, then save a character loadout here.");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(PANEL);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(statusLabel, BorderLayout.SOUTH);

        loadoutSummary = new JPanel(new GridLayout(0, 1, 6, 6));
        refreshAll();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBackground(BG);
        JLabel title = new JLabel("Inventory & Loadouts", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(GOLD);
        header.add(title, BorderLayout.NORTH);

        JPanel filters = new JPanel(new GridLayout(1, 5, 8, 0));
        filters.setBackground(BG);
        for (String filter : new String[]{"All", "Characters", "Weapons", "Artifacts", "Relics"}) {
            JButton btn = new JButton(filter);
            btn.setBackground(TAB);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
            btn.addActionListener(e -> {
                currentFilter = filter;
                refreshCards();
            });
            filters.add(btn);
        }
        header.add(filters, BorderLayout.SOUTH);
        return header;
    }

    private JScrollPane darkScroll(JComponent component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(BorderFactory.createLineBorder(TAB));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildLoadoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel title = new JLabel("Choose Character Loadout", SwingConstants.CENTER);
        title.setForeground(GOLD);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel selectors = new JPanel(new GridLayout(3, 2, 8, 8));
        selectors.setBackground(PANEL);
        characterBox = new JComboBox<>();
        weaponBox = new JComboBox<>();
        artifactBox = new JComboBox<>();
        selectors.add(label("Character")); selectors.add(characterBox);
        selectors.add(label("Weapon")); selectors.add(weaponBox);
        selectors.add(label("Artifact")); selectors.add(artifactBox);
        panel.add(selectors, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 0));
        actions.setBackground(PANEL);
        JButton refresh = goldButton("Refresh");
        refresh.addActionListener(e -> refreshAll());
        JButton save = goldButton("Save To Character");
        save.addActionListener(e -> saveLoadout());
        JButton active = goldButton("Set Active");
        active.addActionListener(e -> setActiveCharacter());
        actions.add(refresh); actions.add(save); actions.add(active);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text + ":");
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JButton goldButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        return b;
    }

    private void refreshAll() {
        refreshCombos();
        refreshCards();
    }

    private void refreshCombos() {
        if (!connector.isOfflineMode()) return;
        var inventory = connector.getOfflineEngine().getInventory();
        characterBox.removeAllItems();
        weaponBox.removeAllItems();
        artifactBox.removeAllItems();
        weaponBox.addItem(new WeaponOption(null));
        artifactBox.addItem(new ArtifactOption(null));
        for (Character c : inventory.getAllCharacters()) characterBox.addItem(c);
        for (InventoryItem item : inventory.getAllItems()) {
            if (item instanceof Weapon w) weaponBox.addItem(new WeaponOption(w));
            if (item instanceof Artifact a) artifactBox.addItem(new ArtifactOption(a));
        }
    }

    private void refreshCards() {
        contentPanel.removeAll();
        if (!connector.isOfflineMode()) {
            contentPanel.add(empty("Offline demo inventory is required."));
            finishRefresh();
            return;
        }
        var inventory = connector.getOfflineEngine().getInventory();
        List<JComponent> cards = new ArrayList<>();
        for (Character c : inventory.getAllCharacters()) {
            if (currentFilter.equals("All") || currentFilter.equals("Characters")) {
                JPanel wrapper = wrapCard(new CharacterCard(c), c.getId());
                wrapper.setToolTipText(loadoutText(c));
                cards.add(wrapper);
            }
        }
        for (InventoryItem item : inventory.getAllItems()) {
            boolean show = currentFilter.equals("All") ||
                (currentFilter.equals("Weapons") && item instanceof Weapon) ||
                (currentFilter.equals("Artifacts") && item instanceof Artifact) ||
                (currentFilter.equals("Relics") && item instanceof Relic);
            if (show) cards.add(wrapCard(new ItemCard(item), null));
        }
        if (cards.isEmpty()) {
            contentPanel.add(empty("No inventory cards here yet. Go to Summon and pull x10 first."));
        } else {
            for (JComponent card : cards) contentPanel.add(card);
        }
        finishRefresh();
    }

    private JPanel wrapCard(JComponent card, String characterId) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(card, BorderLayout.CENTER);
        if (characterId != null) {
            JButton use = new JButton("Use");
            use.setBackground(TAB);
            use.setForeground(Color.WHITE);
            use.setFocusPainted(false);
            use.addActionListener(e -> {
                connector.getOfflineEngine().getLoadoutManager().setActiveCharacter(characterId);
                statusLabel.setText(" Active character selected. Play Game will use its saved weapon/artifact.");
            });
            wrapper.add(use, BorderLayout.SOUTH);
        }
        return wrapper;
    }

    private String loadoutText(Character c) {
        Loadout l = connector.getOfflineEngine().getLoadoutManager().getLoadoutForCharacter(c.getId());
        return l != null ? l.getSummary() : c.getName() + " has no saved weapon/artifact yet.";
    }

    private JLabel empty(String text) {
        JLabel empty = new JLabel("<html><center><h2 style='color:#C9A84C'>" + text + "</h2></center></html>", SwingConstants.CENTER);
        empty.setForeground(Color.WHITE);
        return empty;
    }

    private void finishRefresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void saveLoadout() {
        Character c = (Character) characterBox.getSelectedItem();
        if (c == null) {
            statusLabel.setText(" No characters yet. Summon first.");
            return;
        }
        Weapon w = ((WeaponOption) weaponBox.getSelectedItem()).weapon;
        Artifact a = ((ArtifactOption) artifactBox.getSelectedItem()).artifact;
        Loadout l = connector.getOfflineEngine().getLoadoutManager().saveLoadout(c, w, a);
        statusLabel.setText(" Saved: " + l.getSummary() + ". Play Game will use it automatically.");
        refreshCards();
    }

    private void setActiveCharacter() {
        Character c = (Character) characterBox.getSelectedItem();
        if (c == null) {
            statusLabel.setText(" No character selected.");
            return;
        }
        connector.getOfflineEngine().getLoadoutManager().setActiveCharacter(c.getId());
        statusLabel.setText(" Active: " + c.getName() + ". Press Play Game to start with saved loadout.");
    }

    private static class WeaponOption {
        final Weapon weapon;
        WeaponOption(Weapon weapon) { this.weapon = weapon; }
        public String toString() { return weapon == null ? "No Weapon" : weapon.getName() + " [" + weapon.getRarity() + "] Lv" + weapon.getLevel(); }
    }

    private static class ArtifactOption {
        final Artifact artifact;
        ArtifactOption(Artifact artifact) { this.artifact = artifact; }
        public String toString() { return artifact == null ? "No Artifact" : artifact.getName() + " [" + artifact.getRarity() + "] Lv" + artifact.getLevel(); }
    }
}
