package client.ui.components;

import server.model.abstracts.InventoryItem;
import server.model.items.Artifact;
import server.model.items.Relic;
import server.model.items.Weapon;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ItemCard extends JPanel {
    private final InventoryItem item;

    public ItemCard(InventoryItem item) {
        this.item = item;
        setPreferredSize(new Dimension(150, 200));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(getRarityColor(), 2, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        Color bgColor = getRarityBgColor();
        setBackground(bgColor);

        JLabel nameLabel = new JLabel("<html><center>" + escape(item.getName()) + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));
        centerPanel.setBackground(bgColor);
        addLine(centerPanel, item.getItemType(), getRarityColor(), Font.BOLD);
        addLine(centerPanel, "Lv." + item.getLevel(), Color.LIGHT_GRAY, Font.PLAIN);
        addLine(centerPanel, item.getRarity().toString(), getRarityColor(), Font.BOLD);
        addLine(centerPanel, primaryStat(), Color.WHITE, Font.PLAIN);
        addLine(centerPanel, secondaryStat(), Color.LIGHT_GRAY, Font.PLAIN);
        add(centerPanel, BorderLayout.CENTER);

        add(new StarPanel(item.getRarity()), BorderLayout.SOUTH);
        setToolTipText(item.describeYourself());
    }

    private void addLine(JPanel panel, String text, Color color, int style) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", style, 11));
        label.setForeground(color);
        panel.add(label);
    }

    private String primaryStat() {
        if (item instanceof Weapon weapon) {
            return "DMG " + weapon.getEffectiveDamage();
        }
        if (item instanceof Artifact artifact) {
            return "Bonus +" + artifact.getStatBonus();
        }
        if (item instanceof Relic relic) {
            return "Power " + String.format("%.1f", relic.getTotalPower());
        }
        return "";
    }

    private String secondaryStat() {
        if (item instanceof Weapon weapon) {
            return weapon.getWeaponType().getDisplayName();
        }
        if (item instanceof Artifact artifact) {
            return artifact.getSlot();
        }
        if (item instanceof Relic relic) {
            return relic.getOrigin();
        }
        return "";
    }

    private Color getRarityColor() {
        return switch (item.getRarity()) {
            case LEGENDARY -> new Color(255, 215, 0);
            case RARE -> new Color(79, 195, 247);
            case COMMON -> new Color(136, 136, 136);
        };
    }

    private Color getRarityBgColor() {
        return switch (item.getRarity()) {
            case LEGENDARY -> new Color(40, 35, 15);
            case RARE -> new Color(15, 30, 40);
            case COMMON -> new Color(25, 25, 30);
        };
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public InventoryItem getItem() { return item; }
}
