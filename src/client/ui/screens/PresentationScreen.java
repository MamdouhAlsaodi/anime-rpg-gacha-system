package client.ui.screens;

import javax.swing.*;
import java.awt.*;

public class PresentationScreen extends JPanel {
    private int currentSlide = 0;
    private static final int TOTAL_SLIDES = 6;
    private JLabel slideContent;
    private JLabel slideTitle;
    private JLabel slideIndicator;

    public PresentationScreen() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(10, 10, 15));

        slideTitle = new JLabel("", SwingConstants.CENTER);
        slideTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        slideTitle.setForeground(new Color(201, 168, 76));
        add(slideTitle, BorderLayout.NORTH);

        slideContent = new JLabel("", SwingConstants.CENTER);
        slideContent.setFont(new Font("Monospaced", Font.PLAIN, 13));
        slideContent.setForeground(Color.WHITE);
        slideContent.setVerticalTextPosition(JLabel.TOP);
        add(slideContent, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(15, 15, 25));

        JButton prev = new JButton("< Previous");
        prev.setBackground(new Color(40, 40, 55));
        prev.setForeground(Color.WHITE);
        prev.setFocusPainted(false);
        prev.addActionListener(e -> { if (currentSlide > 0) { currentSlide--; updateSlide(); } });

        slideIndicator = new JLabel("1 / 6");
        slideIndicator.setForeground(new Color(201, 168, 76));
        slideIndicator.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton next = new JButton("Next >");
        next.setBackground(new Color(40, 40, 55));
        next.setForeground(Color.WHITE);
        next.setFocusPainted(false);
        next.addActionListener(e -> { if (currentSlide < TOTAL_SLIDES - 1) { currentSlide++; updateSlide(); } });

        navPanel.add(prev);
        navPanel.add(slideIndicator);
        navPanel.add(next);
        add(navPanel, BorderLayout.SOUTH);

        updateSlide();
    }

    private void updateSlide() {
        slideIndicator.setText((currentSlide + 1) + " / " + TOTAL_SLIDES);
        String[] titles = {
            "Anime RPG Gacha System",
            "3-Layer Architecture",
            "OOP Class Hierarchy",
            "Factory Method Pattern",
            "Gacha System & Probabilities",
            "Custom Exception Handling"
        };
        String[] contents = {
            "<html><div style='text-align:center; padding:30px'>" +
            "<h1 style='color:#C9A84C; font-size:28px'>Anime RPG Gacha System</h1>" +
            "<p style='color:#888; font-size:16px'>A Java-based RPG gacha game with Swing UI</p><br>" +
            "<p style='color:#4FC3F7'>Features:</p>" +
            "<p style='color:white'>- Full gacha summoning system with pity</p>" +
            "<p style='color:white'>- Character collection and inventory</p>" +
            "<p style='color:white'>- TCP Server/Client architecture</p>" +
            "<p style='color:white'>- Animated summoning experience</p>" +
            "<p style='color:white'>- 47 Java classes across 10 phases</p></div></html>",

            "<html><div style='text-align:center; padding:20px'>" +
            "<h2 style='color:#C9A84C'>Client Layer</h2>" +
            "<p style='color:white'>MainScreen | SummonScreen | InventoryScreen</p>" +
            "<p style='color:#4FC3F7'>GachaAnimation | ServerConnector</p><br>" +
            "<h2 style='color:#C9A84C'>API Layer</h2>" +
            "<p style='color:white'>CommandRouter | GameRequest | GameResponse</p>" +
            "<p style='color:#4FC3F7'>SummonHandler | InventoryHandler | PlayerHandler</p><br>" +
            "<h2 style='color:#C9A84C'>Server Layer</h2>" +
            "<p style='color:white'>GameEngine | GachaSystem | Player | Inventory</p>" +
            "<p style='color:#4FC3F7'>CharacterFactory | WeaponFactory | ArtifactFactory</p></div></html>",

            "<html><div style='font-family:monospace; padding:20px'>" +
            "<p style='color:#C9A84C'>BaseEntity (abstract)</p>" +
            "<p style='color:#888'>  |--- Character (abstract)</p>" +
            "<p style='color:white'>  |      |--- Hero</p>" +
            "<p style='color:white'>  |      |--- Support</p>" +
            "<p style='color:white'>  |      |--- Antagonist</p>" +
            "<p style='color:#888'>  |--- InventoryItem (abstract)</p>" +
            "<p style='color:white'>         |--- Weapon</p>" +
            "<p style='color:white'>         |--- Artifact</p>" +
            "<p style='color:white'>         |--- Relic</p><br>" +
            "<p style='color:#C9A84C'>GachaBaseException (abstract)</p>" +
            "<p style='color:white'>  |--- InsufficientGemsException</p>" +
            "<p style='color:white'>  |--- InventoryFullException</p>" +
            "<p style='color:white'>  |--- DuplicateCharacterException</p></div></html>",

            "<html><div style='text-align:center; padding:20px'>" +
            "<h2 style='color:#C9A84C'>SummonFactory&lt;T&gt;</h2>" +
            "<p style='color:#4FC3F7'>Interface: T create(Rarity rarity)</p>" +
            "<p style='color:#888'>default: List&lt;T&gt; createBatch(...)</p><br>" +
            "<p style='color:white'>Implementations:</p>" +
            "<p style='color:#FFD700'>CharacterFactory - creates Hero/Support/Antagonist</p>" +
            "<p style='color:#4FC3F7'>WeaponFactory - creates Weapon items</p>" +
            "<p style='color:#4CAF50'>ArtifactFactory - creates Artifact items</p><br>" +
            "<p style='color:#888'>Each factory respects rarity scaling</p>" +
            "<p style='color:#888'>and provides randomized attributes</p></div></html>",

            "<html><div style='text-align:center; padding:20px'>" +
            "<h2 style='color:#C9A84C'>Gacha Rates</h2>" +
            "<p style='color:#FFD700; font-size:18px'>Legendary: 0.6% (Hard Pity: 90 pulls)</p>" +
            "<p style='color:#4FC3F7; font-size:18px'>Rare: 5.1% (Soft Pity: 10 pulls)</p>" +
            "<p style='color:#888; font-size:18px'>Common: 94.3%</p><br>" +
            "<p style='color:white'>Pity System:</p>" +
            "<p style='color:white'>- Soft pity increases legendary rate after 70 pulls</p>" +
            "<p style='color:white'>- Guaranteed rare every 10 pulls</p>" +
            "<p style='color:white'>- Guaranteed legendary every 90 pulls</p><br>" +
            "<p style='color:#C9A84C'>Cost: 160 gems per pull | 1600 for x10</p></div></html>",

            "<html><div style='text-align:center; padding:20px'>" +
            "<h2 style='color:#C9A84C'>Exception Hierarchy</h2>" +
            "<p style='color:white'>GachaBaseException (abstract)</p>" +
            "<p style='color:white'>  errorCode + errorTime + getErrorCategory()</p><br>" +
            "<p style='color:#FF5722'>InsufficientGemsException [1001]</p>" +
            "<p style='color:#888'>Category: ECONOMY - not enough gems</p><br>" +
            "<p style='color:#FFEB3B'>InventoryFullException [1002]</p>" +
            "<p style='color:#888'>Category: INVENTORY - storage exceeded</p><br>" +
            "<p style='color:#4FC3F7'>DuplicateCharacterException [1003]</p>" +
            "<p style='color:#888'>Category: CONSTELLATION - triggers upgrade</p></div></html>"
        };
        slideTitle.setText(titles[currentSlide]);
        slideContent.setText(contents[currentSlide]);
    }
}
