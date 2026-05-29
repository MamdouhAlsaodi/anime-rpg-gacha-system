package client.ui.screens;

import server.model.game2d.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PresentationScreen extends JPanel {
    private static final Color BG = new Color(10, 10, 15);
    private static final Color PANEL = new Color(18, 18, 30);
    private static final Color PANEL_2 = new Color(24, 24, 40);
    private static final Color GOLD = new Color(201, 168, 76);
    private static final Color BLUE = new Color(79, 195, 247);
    private static final Color GREEN = new Color(76, 175, 80);
    private static final Color RED = new Color(255, 112, 67);

    private int currentSlide = 0;
    private static final int TOTAL_SLIDES = 8;
    private JLabel slideTitle;
    private JLabel slideIndicator;
    private JPanel bodyHost;
    private JButton prev;
    private JButton next;

    public PresentationScreen() {
        setLayout(new BorderLayout(12, 12));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        slideTitle = new JLabel("", SwingConstants.CENTER);
        slideTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        slideTitle.setForeground(GOLD);
        slideTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        add(slideTitle, BorderLayout.NORTH);

        bodyHost = new JPanel(new BorderLayout());
        bodyHost.setOpaque(false);
        add(bodyHost, BorderLayout.CENTER);

        add(createNavPanel(), BorderLayout.SOUTH);
        updateSlide();
    }

    @Override
    public void doLayout() {
        super.doLayout();
        layoutChildrenRecursively(this);
    }

    private static void layoutChildrenRecursively(Container container) {
        for (Component child : container.getComponents()) {
            if (child instanceof Container childContainer) {
                childContainer.doLayout();
                layoutChildrenRecursively(childContainer);
            }
        }
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel(new BorderLayout(12, 0));
        navPanel.setBackground(new Color(15, 15, 25));
        navPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        prev = makeButton("◀ Previous");
        prev.addActionListener(e -> { if (currentSlide > 0) { currentSlide--; updateSlide(); } });

        next = makeButton("Next ▶");
        next.addActionListener(e -> { if (currentSlide < TOTAL_SLIDES - 1) { currentSlide++; updateSlide(); } });

        slideIndicator = new JLabel("", SwingConstants.CENTER);
        slideIndicator.setForeground(GOLD);
        slideIndicator.setFont(new Font("Segoe UI", Font.BOLD, 15));

        navPanel.add(prev, BorderLayout.WEST);
        navPanel.add(slideIndicator, BorderLayout.CENTER);
        navPanel.add(next, BorderLayout.EAST);
        return navPanel;
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PANEL_2);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD.darker(), 1),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateSlide() {
        slideIndicator.setText((currentSlide + 1) + " / " + TOTAL_SLIDES + progressText());
        prev.setEnabled(currentSlide > 0);
        next.setEnabled(currentSlide < TOTAL_SLIDES - 1);

        bodyHost.removeAll();
        switch (currentSlide) {
            case 0 -> showHeroSlide();
            case 1 -> showFeatureSlide();
            case 2 -> showArchitectureSlide();
            case 3 -> showLoopSlide();
            case 4 -> showFiveStagesSlide();
            case 5 -> showOopSlide();
            case 6 -> showGachaSlide();
            case 7 -> showFinalSlide();
            default -> showHeroSlide();
        }
        bodyHost.revalidate();
        bodyHost.repaint();
    }

    private String progressText() {
        StringBuilder bar = new StringBuilder("   ");
        for (int i = 0; i < TOTAL_SLIDES; i++) bar.append(i <= currentSlide ? "●" : "○");
        return bar.toString();
    }

    private void setTitle(String title) {
        slideTitle.setText(title);
    }

    private void showHeroSlide() {
        setTitle("Anime RPG Gacha System — Showcase");
        JPanel panel = verticalCard();
        panel.add(label("Anime RPG Gacha System", 34, GOLD, true));
        panel.add(label("Java Swing RPG + Gacha + 2D Adventure", 18, BLUE, false));
        panel.add(Box.createVerticalStrut(18));
        panel.add(label("New update: the collection finally turns into gameplay.", 18, Color.WHITE, false));
        panel.add(label("Summon heroes → choose loadout → clear five stages → earn gems.", 17, new Color(220, 220, 220), false));
        panel.add(Box.createVerticalStrut(22));
        panel.add(new StagePreviewPanel(0, true));
        bodyHost.add(panel, BorderLayout.CENTER);
    }

    private void showFeatureSlide() {
        setTitle("Updated Features");
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(feature("Summon", "Single/x10 pulls, rarity colors, pity system", GOLD));
        grid.add(feature("Inventory", "Characters, weapons, artifacts, relics", BLUE));
        grid.add(feature("Progression", "Levels, enhancement, constellations", GREEN));
        grid.add(feature("Resources", "Daily login, quests, missions, achievements", new Color(255, 193, 7)));
        grid.add(feature("2D Adventure", "Playable combat with waves, bosses, rewards", RED));
        grid.add(feature("Presentation", "In-app project showcase and stage previews", new Color(186, 104, 200)));
        bodyHost.add(grid, BorderLayout.CENTER);
    }

    private void showArchitectureSlide() {
        setTitle("3-Layer Architecture");
        JPanel grid = new JPanel(new GridLayout(1, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(feature("Client Layer", "MainScreen\nSummonScreen\nInventoryScreen\nGamePanel\nPresentationScreen", GOLD));
        grid.add(feature("API Layer", "GameRequest\nGameResponse\nCommandRouter\nHandlers", BLUE));
        grid.add(feature("Server Layer", "GameEngine\nGachaSystem\nInventory\nFactories\nResources", GREEN));
        bodyHost.add(grid, BorderLayout.CENTER);
    }

    private void showLoopSlide() {
        setTitle("Core Gameplay Loop");
        JPanel panel = verticalCard();
        panel.add(label("The loop is now clear and playable:", 22, GOLD, true));
        panel.add(Box.createVerticalStrut(16));
        panel.add(label("1. Claim resources and summon characters", 18, Color.WHITE, false));
        panel.add(label("2. Pick your hero and weapon", 18, Color.WHITE, false));
        panel.add(label("3. Enter Adventure Mode", 18, Color.WHITE, false));
        panel.add(label("4. Fight waves and bosses", 18, Color.WHITE, false));
        panel.add(label("5. Earn gems, upgrade, and repeat", 18, Color.WHITE, false));
        panel.add(Box.createVerticalStrut(24));
        panel.add(label("This makes every summon matter in battle.", 20, BLUE, true));
        bodyHost.add(panel, BorderLayout.CENTER);
    }

    private void showFiveStagesSlide() {
        setTitle("Five-Stage Adventure Teaser");
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        JPanel previews = new JPanel(new GridLayout(1, 5, 8, 8));
        previews.setOpaque(false);
        for (int i = 1; i <= 5; i++) previews.add(new StagePreviewPanel(i, false));
        panel.add(previews, BorderLayout.CENTER);
        JLabel hint = label("Each stage has its own mood, enemies, colors, and boss pressure.", 17, Color.WHITE, false);
        panel.add(hint, BorderLayout.SOUTH);
        bodyHost.add(panel, BorderLayout.CENTER);
    }

    private void showOopSlide() {
        setTitle("OOP Design Highlights");
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);
        grid.add(feature("Abstraction", "BaseEntity, Character, InventoryItem, GachaBaseException", GOLD));
        grid.add(feature("Inheritance", "Hero, Support, Antagonist extend Character", BLUE));
        grid.add(feature("Polymorphism", "Factories and inventories use abstract types", GREEN));
        grid.add(feature("Encapsulation", "Player gems, inventory maps, engine services", RED));
        bodyHost.add(grid, BorderLayout.CENTER);
    }

    private void showGachaSlide() {
        setTitle("Gacha + Resource Systems");
        JPanel grid = new JPanel(new GridLayout(1, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(feature("Rates", "Legendary 0.6%\nRare 5.1%\nCommon 94.3%", GOLD));
        grid.add(feature("Pity", "Rare every 10 pulls\nLegendary hard pity at 90", BLUE));
        grid.add(feature("Rewards", "Adventure gems\nDaily missions\nAchievements\nQuest rewards", GREEN));
        bodyHost.add(grid, BorderLayout.CENTER);
    }

    private void showFinalSlide() {
        setTitle("Ready for Demo");
        JPanel panel = verticalCard();
        panel.add(label("Demo Path", 30, GOLD, true));
        panel.add(Box.createVerticalStrut(18));
        panel.add(label("Summon x10 → Inventory → Player Stats → Play Game → Presentation", 19, Color.WHITE, true));
        panel.add(Box.createVerticalStrut(18));
        panel.add(label("The project now looks like a complete game, not only menus.", 19, BLUE, false));
        panel.add(Box.createVerticalStrut(26));
        panel.add(new StagePreviewPanel(5, true));
        bodyHost.add(panel, BorderLayout.CENTER);
    }

    private JPanel verticalCard() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        return panel;
    }

    private JLabel label(String text, int size, Color color, boolean bold) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return l;
    }

    private JPanel feature(String title, String text, Color accent) {
        JPanel p = new RoundedPanel(PANEL, accent);
        p.setLayout(new BorderLayout(6, 8));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(accent);
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(Color.WHITE);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        p.add(t, BorderLayout.NORTH);
        p.add(area, BorderLayout.CENTER);
        return p;
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color border;
        RoundedPanel(Color fill, Color border) {
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, 22, 22));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-3, getHeight()-3, 22, 22));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class StagePreviewPanel extends JPanel {
        private final int stageNum;
        private final boolean big;

        StagePreviewPanel(int stageNum, boolean big) {
            this.stageNum = stageNum;
            this.big = big;
            setOpaque(false);
            setPreferredSize(new Dimension(big ? 640 : 130, big ? 210 : 230));
            setMinimumSize(new Dimension(big ? 500 : 120, big ? 180 : 200));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int stage = stageNum == 0 ? 1 + (int)((System.currentTimeMillis() / 1400) % 5) : stageNum;
            Stage s = Stage.createStage(stage);

            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillRoundRect(0, 0, w, h, 20, 20);
            g2.setColor(s.getBgColor());
            g2.fillRoundRect(6, 6, w - 12, h - 12, 18, 18);

            int ground = h - 48;
            drawScene(g2, stage, w, h, ground, s.getGroundColor());

            g2.setColor(new Color(0, 0, 0, 130));
            g2.fillRoundRect(10, 10, w - 20, big ? 42 : 52, 16, 16);
            g2.setColor(GOLD);
            g2.setFont(new Font("Segoe UI", Font.BOLD, big ? 20 : 13));
            String name = "Stage " + stage + " — " + s.getName();
            drawCentered(g2, name, w / 2, big ? 37 : 30);
            if (!big) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(Color.WHITE);
                drawCentered(g2, s.getTotalWaves() + " waves  •  " + s.getRewardGems() + " gems", w / 2, 48);
            }

            g2.setColor(new Color(255, 255, 255, 95));
            g2.drawRoundRect(6, 6, w - 12, h - 12, 18, 18);
            g2.dispose();

            if (stageNum == 0) repaint(1200);
        }

        private void drawScene(Graphics2D g, int stage, int w, int h, int ground, Color groundColor) {
            g.setColor(groundColor);
            g.fillRect(6, ground, w - 12, h - ground - 6);
            switch (stage) {
                case 1 -> drawForest(g, w, ground);
                case 2 -> drawDesert(g, w, ground);
                case 3 -> drawCave(g, w, ground);
                case 4 -> drawVolcano(g, w, ground);
                case 5 -> drawCastle(g, w, ground);
                default -> drawForest(g, w, ground);
            }
            drawHeroAndEnemy(g, w, ground, stage);
        }

        private void drawForest(Graphics2D g, int w, int ground) {
            for (int i = 0; i < 7; i++) {
                int x = 12 + i * Math.max(24, w / 7);
                int th = 38 + (i % 3) * 12;
                g.setColor(new Color(83, 54, 31));
                g.fillRect(x, ground - th, 8, th);
                g.setColor(new Color(28, 115 + i * 7, 45));
                g.fillOval(x - 17, ground - th - 25, 42, 38);
            }
        }

        private void drawDesert(Graphics2D g, int w, int ground) {
            g.setColor(new Color(245, 196, 97));
            for (int i = 0; i < 5; i++) g.fillOval(i * w / 4 - 20, ground - 18 + i % 2 * 4, w / 3, 32);
            g.setColor(new Color(62, 150, 91));
            int cx = w - 52;
            g.fillRoundRect(cx, ground - 58, 12, 58, 8, 8);
            g.fillRoundRect(cx - 16, ground - 40, 10, 28, 8, 8);
            g.fillRoundRect(cx + 18, ground - 47, 10, 34, 8, 8);
        }

        private void drawCave(Graphics2D g, int w, int ground) {
            g.setColor(new Color(38, 31, 52));
            for (int i = 0; i < 8; i++) {
                int x = i * w / 8;
                g.fillPolygon(new int[]{x, x + 20, x + 40}, new int[]{6, 58 + (i % 3) * 14, 6}, 3);
            }
            g.setColor(new Color(98, 78, 126));
            g.fillOval(w / 2 - 42, ground - 64, 84, 72);
        }

        private void drawVolcano(Graphics2D g, int w, int ground) {
            g.setColor(new Color(82, 35, 24));
            g.fillPolygon(new int[]{w / 2 - 110, w / 2, w / 2 + 120}, new int[]{ground, 34, ground}, 3);
            g.setColor(new Color(255, 112, 32));
            g.fillOval(w / 2 - 20, 28, 40, 18);
            g.setColor(new Color(255, 72, 24));
            for (int i = 0; i < 4; i++) g.fillRoundRect(w / 2 - 8 + i * 9, 48 + i * 15, 8, 54, 8, 8);
        }

        private void drawCastle(Graphics2D g, int w, int ground) {
            g.setColor(new Color(58, 52, 78));
            int x = w / 2 - 70;
            g.fillRect(x, ground - 92, 140, 92);
            g.fillRect(x + 12, ground - 126, 34, 42);
            g.fillRect(x + 94, ground - 126, 34, 42);
            g.setColor(new Color(205, 176, 86));
            g.fillRoundRect(w / 2 - 18, ground - 42, 36, 42, 12, 12);
            g.setColor(new Color(140, 28, 42));
            g.fillRect(w / 2 - 8, ground - 28, 16, 12);
        }

        private void drawHeroAndEnemy(Graphics2D g, int w, int ground, int stage) {
            int heroX = Math.max(28, w / 5);
            g.setColor(new Color(85, 190, 255));
            g.fillOval(heroX, ground - 52, 26, 26);
            g.setColor(new Color(60, 120, 230));
            g.fillRoundRect(heroX + 4, ground - 28, 18, 28, 8, 8);
            g.setColor(GOLD);
            g.drawLine(heroX + 26, ground - 22, heroX + 48, ground - 35);

            int enemyX = Math.min(w - 58, w * 3 / 4);
            g.setColor(stage == 5 ? new Color(180, 40, 160) : new Color(230, 70, 55));
            g.fillOval(enemyX, ground - 44, 30, 30);
            g.setColor(Color.WHITE);
            g.fillOval(enemyX + 8, ground - 35, 5, 5);
            g.fillOval(enemyX + 18, ground - 35, 5, 5);
        }

        private static void drawCentered(Graphics2D g, String text, int x, int y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, x - fm.stringWidth(text) / 2, y);
        }
    }
}
