package client.ui.screens;

import client.network.ServerConnector;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.player.Player;
import server.service.UpgradeService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpgradeScreen extends JPanel {
    private final ServerConnector connector;
    private final Runnable refreshGems;
    private final UpgradeService upgradeService = new UpgradeService();

    private JLabel currencyLabel;
    private JLabel statusLabel;
    private JComboBox<String> characterBox;
    private JComboBox<String> itemBox;
    private JTextArea previewArea;
    private List<Character> characters = new ArrayList<>();
    private List<InventoryItem> items = new ArrayList<>();

    private static final Color BG = new Color(10, 10, 15);
    private static final Color PANEL = new Color(20, 20, 30);
    private static final Color PANEL_2 = new Color(30, 30, 45);
    private static final Color GOLD = new Color(201, 168, 76);

    public UpgradeScreen(ServerConnector connector, Runnable refreshGems) {
        this.connector = connector;
        this.refreshGems = refreshGems;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        setBackground(BG);
        buildUI();
        refresh();
    }

    private void buildUI() {
        JLabel title = new JLabel("Upgrade Workshop — Characters / Weapons", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(GOLD);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.setBackground(BG);
        center.add(characterPanel());
        center.add(itemPanel());
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setBackground(BG);
        currencyLabel = new JLabel(" ", SwingConstants.CENTER);
        currencyLabel.setOpaque(true);
        currencyLabel.setBackground(PANEL);
        currencyLabel.setForeground(Color.WHITE);
        currencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        footer.add(currencyLabel, BorderLayout.NORTH);

        previewArea = new JTextArea(4, 40);
        previewArea.setEditable(false);
        previewArea.setBackground(new Color(15, 15, 22));
        previewArea.setForeground(new Color(230, 230, 230));
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        previewArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        footer.add(previewArea, BorderLayout.CENTER);

        statusLabel = new JLabel(" Select a unit or weapon, then spend C/R to upgrade.");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(PANEL);
        statusLabel.setForeground(new Color(220, 220, 220));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        footer.add(statusLabel, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel characterPanel() {
        JPanel panel = cardPanel("Character Upgrade");
        characterBox = new JComboBox<>();
        characterBox.addActionListener(e -> updatePreview());
        panel.add(styledLabel("Choose hero:"));
        panel.add(characterBox);
        JButton upgrade = actionButton("Upgrade Character +1 Lv");
        upgrade.addActionListener(e -> upgradeCharacter());
        panel.add(upgrade);
        return panel;
    }

    private JPanel itemPanel() {
        JPanel panel = cardPanel("Weapon / Item Enhance");
        itemBox = new JComboBox<>();
        itemBox.addActionListener(e -> updatePreview());
        panel.add(styledLabel("Choose weapon/item:"));
        panel.add(itemBox);
        JButton upgrade = actionButton("Enhance Item +1 Lv");
        upgrade.addActionListener(e -> upgradeItem());
        panel.add(upgrade);
        JButton refresh = actionButton("Refresh Inventory");
        refresh.addActionListener(e -> refresh());
        panel.add(refresh);
        return panel;
    }

    private JPanel cardPanel(String titleText) {
        JPanel fields = new JPanel(new GridLayout(0, 1, 8, 8));
        fields.setBackground(PANEL);
        fields.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GOLD),
                titleText,
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 18),
                GOLD
            ),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        return fields;
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private JButton actionButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PANEL_2);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return b;
    }

    private void refresh() {
        if (!connector.isOfflineMode() || connector.getOfflineEngine() == null) return;
        characters = new ArrayList<>(connector.getOfflineEngine().getInventory().getAllCharacters());
        items = new ArrayList<>(connector.getOfflineEngine().getInventory().getAllItems());

        characterBox.removeAllItems();
        if (characters.isEmpty()) {
            characterBox.addItem("No characters yet — summon first");
        } else {
            for (Character c : characters) {
                characterBox.addItem(c.getName() + " [" + c.getRarity() + "] Lv" + c.getLevel());
            }
        }

        itemBox.removeAllItems();
        if (items.isEmpty()) {
            itemBox.addItem("No weapons/items yet — summon first");
        } else {
            for (InventoryItem item : items) {
                itemBox.addItem(item.getName() + " [" + item.getItemType() + " / " + item.getRarity() + "] Lv" + item.getLevel());
            }
        }
        updateCurrency();
        updatePreview();
    }

    private void updateCurrency() {
        Player p = connector.getOfflineEngine().getPlayer();
        currencyLabel.setText(String.format("Gems: %,d    |    C: %,d    |    R: %,d", p.getGems(), p.getCommonCurrency(), p.getRareCurrency()));
    }

    private void updatePreview() {
        if (previewArea == null || !connector.isOfflineMode()) return;
        StringBuilder sb = new StringBuilder();
        if (!characters.isEmpty() && characterBox.getSelectedIndex() >= 0) {
            Character c = characters.get(characterBox.getSelectedIndex());
            sb.append("Character: ").append(c.getName()).append(" Lv").append(c.getLevel())
              .append(" | next cost: ").append(upgradeService.getCharacterCost(c)).append('\n');
            sb.append("Stats after upgrade: HP ").append((c.getLevel() + 1) * 100)
              .append(" | ATK +10-ish | DEF +5").append('\n');
        } else {
            sb.append("Character: summon first to unlock hero upgrades.\n");
        }
        if (!items.isEmpty() && itemBox.getSelectedIndex() >= 0) {
            InventoryItem item = items.get(itemBox.getSelectedIndex());
            sb.append("Item: ").append(item.getName()).append(" Lv").append(item.getLevel())
              .append(" | next cost: ").append(upgradeService.getItemCost(item)).append('\n');
        } else {
            sb.append("Item: summon first to unlock weapon/item enhancements.\n");
        }
        previewArea.setText(sb.toString());
    }

    private void upgradeCharacter() {
        if (characters.isEmpty()) {
            statusLabel.setText(" No characters yet. Summon first, then come back to upgrade.");
            return;
        }
        Character c = characters.get(characterBox.getSelectedIndex());
        String msg = upgradeService.upgradeCharacter(connector.getOfflineEngine().getPlayer(), c);
        connector.getOfflineEngine().getResourceSystem().onLevelUp();
        statusLabel.setText(" " + msg);
        refreshGems.run();
        refresh();
    }

    private void upgradeItem() {
        if (items.isEmpty()) {
            statusLabel.setText(" No weapons/items yet. Summon first, then enhance them here.");
            return;
        }
        InventoryItem item = items.get(itemBox.getSelectedIndex());
        String msg = upgradeService.upgradeItem(connector.getOfflineEngine().getPlayer(), item);
        connector.getOfflineEngine().getResourceSystem().onEnhance();
        statusLabel.setText(" " + msg);
        refreshGems.run();
        refresh();
    }
}
