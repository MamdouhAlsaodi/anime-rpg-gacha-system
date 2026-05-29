package client.ui;

import server.model.game2d.*;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.enums.Rarity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel implements ActionListener {
    private PlayerEntity player;
    private CombatEngine combat;
    private int currentStageNum;
    private GameState state;
    private Set<Integer> keys;
    private Timer gameTimer;
    private int groundY;
    private JFrame parentFrame;

    // Stage select info
    private String selectedCharName;
    private Rarity selectedCharRarity;
    private String selectedWeapon;
    private Character gameCharacter;
    private InventoryItem gameWeapon;
    private int totalGemsEarned;
    private int totalKills;

    // Decorations
    private int[] treeX, treeH;
    private int[] cloudX, cloudY;

    private enum GameState { STAGE_INTRO, PLAYING, WAVE_CLEAR, STAGE_COMPLETE, GAME_OVER, VICTORY }

    public GamePanel(JFrame frame, Character character, InventoryItem weapon) {
        this.parentFrame = frame;
        this.gameCharacter = character;
        this.gameWeapon = weapon;
        this.currentStageNum = 1;
        this.totalGemsEarned = 0;
        this.totalKills = 0;
        this.keys = new HashSet<>();
        this.state = GameState.STAGE_INTRO;
        this.groundY = 420;

        setPreferredSize(new Dimension(800, 500));
        setFocusable(true);

        // Init decorations
        treeX = new int[12]; treeH = new int[12];
        for (int i = 0; i < 12; i++) { treeX[i] = i * 70 + 10; treeH[i] = 40 + (int)(Math.random()*30); }
        cloudX = new int[5]; cloudY = new int[5];
        for (int i = 0; i < 5; i++) { cloudX[i] = i * 170 + 20; cloudY[i] = 30 + (int)(Math.random()*40); }

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { keys.add(e.getKeyCode()); handleKey(e.getKeyCode()); }
            public void keyReleased(KeyEvent e) { keys.remove(e.getKeyCode()); }
        });
    }

    public void startStage(int stageNum) {
        this.currentStageNum = stageNum;
        Stage stage = Stage.createStage(stageNum);
        player = new PlayerEntity(gameCharacter, gameWeapon);
        player.setX(100); player.setY(groundY);
        combat = new CombatEngine(player, stage);
        state = GameState.PLAYING;
        gameTimer = new Timer(33, this); // ~30 FPS
        gameTimer.start();
    }

    private void handleKey(int code) {
        if (state == GameState.STAGE_INTRO && code == KeyEvent.VK_ENTER) {
            startStage(currentStageNum);
        }
        if (state == GameState.STAGE_COMPLETE && code == KeyEvent.VK_ENTER) {
            if (currentStageNum < 5) {
                currentStageNum++;
                state = GameState.STAGE_INTRO;
            } else {
                state = GameState.VICTORY;
            }
        }
        if (state == GameState.GAME_OVER && code == KeyEvent.VK_ENTER) {
            parentFrame.dispose();
        }
        if (state == GameState.VICTORY && code == KeyEvent.VK_ENTER) {
            parentFrame.dispose();
        }
        if (state == GameState.PLAYING && code == KeyEvent.VK_SPACE) {
            player.attack();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state != GameState.PLAYING) return;

        // Movement
        int dx = 0, dy = 0;
        int spd = 4;
        if (keys.contains(KeyEvent.VK_W) || keys.contains(KeyEvent.VK_UP)) dy -= spd;
        if (keys.contains(KeyEvent.VK_S) || keys.contains(KeyEvent.VK_DOWN)) dy += spd;
        if (keys.contains(KeyEvent.VK_A) || keys.contains(KeyEvent.VK_LEFT)) dx -= spd;
        if (keys.contains(KeyEvent.VK_D) || keys.contains(KeyEvent.VK_RIGHT)) dx += spd;
        player.move(dx, dy, getWidth(), getHeight(), groundY);

        // Attack
        if (keys.contains(KeyEvent.VK_SPACE)) player.attack();

        // Update combat
        combat.update(getWidth(), getHeight());

        // Check game over
        if (combat.isPlayerDead()) {
            state = GameState.GAME_OVER;
            gameTimer.stop();
        }
        if (combat.isStageComplete()) {
            state = GameState.STAGE_COMPLETE;
            totalGemsEarned += combat.getGemsEarned();
            totalKills += player.getKills();
            gameTimer.stop();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (state) {
            case STAGE_INTRO -> drawStageIntro(g2);
            case PLAYING -> drawGame(g2);
            case STAGE_COMPLETE -> drawStageComplete(g2);
            case GAME_OVER -> drawGameOver(g2);
            case VICTORY -> drawVictory(g2);
            default -> {}
        }
    }

    private void drawStageIntro(Graphics2D g) {
        Stage stage = Stage.createStage(currentStageNum);
        g.setColor(stage.getBgColor()); g.fillRect(0, 0, getWidth(), getHeight());

        // Dark readable overlay
        g.setColor(new Color(0, 0, 0, 95));
        g.fillRoundRect(48, 28, getWidth() - 96, getHeight() - 56, 28, 28);
        g.setColor(new Color(201, 168, 76));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(48, 28, getWidth() - 96, getHeight() - 56, 28, 28);

        g.setColor(new Color(201, 168, 76));
        g.setFont(new Font("SansSerif", Font.BOLD, 34));
        String title = "Stage " + currentStageNum + " Preview";
        g.drawString(title, getWidth()/2 - g.getFontMetrics().stringWidth(title)/2, 82);

        g.setFont(new Font("SansSerif", Font.BOLD, 23));
        g.setColor(Color.WHITE);
        g.drawString(stage.getName(), getWidth()/2 - g.getFontMetrics().stringWidth(stage.getName())/2, 116);

        drawStageTeaser(g, stage, 115, 140, getWidth() - 230, 190);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Waves: " + stage.getTotalWaves() + "  |  Enemies: " + stage.getEnemiesPerWave() + "/wave", 230, 365);
        g.drawString("Enemy HP: " + stage.getEnemyBaseHP() + "  |  Reward: " + stage.getRewardGems() + " Gems", 230, 390);
        if (stage.hasBoss()) {
            g.setColor(new Color(255, 215, 0));
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("BOSS BATTLE AHEAD!", 455, 378);
        }

        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        String press = "Press ENTER to start the stage";
        g.drawString(press, getWidth()/2 - g.getFontMetrics().stringWidth(press)/2, 445);
    }

    private void drawStageTeaser(Graphics2D g, Stage stage, int x, int y, int w, int h) {
        int oldGround = groundY;
        groundY = y + h - 55;

        g.setColor(new Color(0, 0, 0, 130));
        g.fillRoundRect(x - 6, y - 6, w + 12, h + 12, 20, 20);
        g.setColor(stage.getBgColor());
        g.fillRoundRect(x, y, w, h, 18, 18);
        g.setClip(x, y, w, h);

        g.setColor(stage.getGroundColor());
        g.fillRect(x, groundY + 24, w, h - 24);
        drawDecorations(g, stage);
        drawMiniHero(g, x + 80, groundY + 24);
        drawMiniEnemy(g, x + w - 130, groundY + 24, stage.hasBoss());

        g.setClip(null);
        g.setColor(new Color(255, 255, 255, 100));
        g.drawRoundRect(x, y, w, h, 18, 18);
        groundY = oldGround;
    }

    private void drawMiniHero(Graphics2D g, int x, int feetY) {
        g.setColor(new Color(85, 190, 255));
        g.fillOval(x, feetY - 58, 30, 30);
        g.setColor(new Color(60, 120, 230));
        g.fillRoundRect(x + 5, feetY - 30, 20, 30, 8, 8);
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3f));
        g.drawLine(x + 28, feetY - 24, x + 60, feetY - 43);
    }

    private void drawMiniEnemy(Graphics2D g, int x, int feetY, boolean boss) {
        g.setColor(boss ? new Color(180, 40, 160) : new Color(230, 70, 55));
        g.fillOval(x, feetY - (boss ? 72 : 52), boss ? 58 : 38, boss ? 58 : 38);
        g.setColor(Color.WHITE);
        g.fillOval(x + 12, feetY - (boss ? 50 : 35), 7, 7);
        g.fillOval(x + (boss ? 36 : 24), feetY - (boss ? 50 : 35), 7, 7);
        g.setColor(new Color(0, 0, 0, 130));
        g.drawLine(x + 12, feetY - 18, x + (boss ? 46 : 30), feetY - 18);
    }

    private void drawGame(Graphics2D g) {
        Stage stage = combat.getStage();

        // Sky
        g.setColor(stage.getBgColor()); g.fillRect(0, 0, getWidth(), getHeight());

        // Ground
        g.setColor(stage.getGroundColor()); g.fillRect(0, groundY + 30, getWidth(), getHeight() - groundY - 30);

        // Decorations based on theme
        drawDecorations(g, stage);

        // Draw enemies
        for (Enemy2D e : combat.getEnemies()) e.draw(g);

        // Draw player
        player.draw(g);

        // HUD
        drawHUD(g);
    }

    private void drawDecorations(Graphics2D g, Stage stage) {
        switch (stage.getTheme()) {
            case FOREST -> {
                g.setColor(new Color(20, 80, 20));
                for (int i = 0; i < treeX.length; i++) {
                    g.fillRect(treeX[i], groundY + 30 - treeH[i], 10, treeH[i]);
                    g.setColor(new Color(30, 100 + i*5, 30));
                    g.fillOval(treeX[i] - 15, groundY + 30 - treeH[i] - 20, 40, 30);
                    g.setColor(new Color(20, 80, 20));
                }
            }
            case DESERT -> {
                g.setColor(new Color(210, 170, 90));
                for (int i = 0; i < 5; i++) {
                    int cx = i * 180 + 40;
                    g.fillOval(cx, groundY + 10, 80, 30);
                    g.fillOval(cx + 20, groundY - 5, 40, 30);
                }
            }
            case CAVE -> {
                g.setColor(new Color(50, 40, 60));
                for (int i = 0; i < 8; i++) {
                    int sx = i * 110 + 20;
                    g.fillOval(sx, 0, 60, 50);
                    g.fillRect(sx + 5, 40, 15, 30 + i * 5);
                }
            }
            case VOLCANO -> {
                g.setColor(new Color(100, 30, 10));
                g.fillPolygon(new int[]{300, 500, 600, 200}, new int[]{50, 50, groundY+30, groundY+30}, 4);
                g.setColor(new Color(255, 100, 0));
                g.fillOval(420, 30, 30, 30);
            }
            case CASTLE -> {
                g.setColor(new Color(60, 50, 70));
                g.fillRect(600, groundY - 100, 180, 130);
                g.fillRect(620, groundY - 140, 40, 50);
                g.fillRect(720, groundY - 140, 40, 50);
                g.setColor(new Color(200, 180, 100));
                g.fillRect(665, groundY - 40, 50, 70);
                g.setColor(Color.RED);
                g.fillRect(680, groundY - 30, 20, 15);
            }
        }
    }

    private void drawHUD(Graphics2D g) {
        // Background bar
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), 50);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(Color.WHITE);

        // Stage info
        g.drawString("Stage " + currentStageNum + " | Wave " + combat.getCurrentWave() + "/" + combat.getStage().getTotalWaves(), 10, 20);

        // Player HP
        g.setColor(Color.GREEN);
        g.drawString("HP: " + player.getHP() + "/" + player.getMaxHP(), 10, 40);
        g.setColor(Color.RED); g.fillRect(180, 28, 120, 12);
        g.setColor(Color.GREEN); g.fillRect(180, 28, (int)(120 * (double)player.getHP()/player.getMaxHP()), 12);

        // Gems earned
        g.setColor(Color.YELLOW);
        g.drawString("Gems: +" + combat.getGemsEarned(), 350, 20);

        // Kills
        g.setColor(Color.WHITE);
        g.drawString("Kills: " + player.getKills(), 350, 40);

        // ATK
        g.setColor(Color.ORANGE);
        g.drawString("ATK: " + player.getTotalAttack(), 500, 20);

        // Controls hint
        g.setColor(Color.GRAY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("WASD/Arrows: Move | SPACE: Attack", 500, 40);

        // Combat log
        if (combat.getLogTimer() > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("SansSerif", Font.BOLD, 20));
            String log = combat.getCombatLog();
            g.drawString(log, getWidth()/2 - log.length()*6, 80);
        }
    }

    private void drawStageComplete(Graphics2D g) {
        g.setColor(new Color(0, 40, 0)); g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.drawString("STAGE COMPLETE!", 200, 180);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString("Gems Earned: " + combat.getGemsEarned(), 280, 240);
        g.drawString("Enemies Killed: " + player.getKills(), 280, 270);
        if (currentStageNum < 5) {
            g.setColor(Color.GREEN);
            g.drawString("Press ENTER for Stage " + (currentStageNum + 1), 260, 340);
        } else {
            g.setColor(new Color(255, 215, 0));
            g.drawString("ALL STAGES COMPLETE!", 260, 340);
        }
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(60, 0, 0)); g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("GAME OVER", 240, 200);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.drawString("You reached Stage " + currentStageNum, 290, 260);
        g.drawString("Gems: " + totalGemsEarned + " | Kills: " + totalKills, 280, 290);
        g.drawString("Press ENTER to return", 300, 360);
    }

    private void drawVictory(Graphics2D g) {
        g.setColor(new Color(20, 10, 50)); g.fillRect(0, 0, getWidth(), getHeight());
        // Stars
        g.setColor(Color.YELLOW);
        for (int i = 0; i < 50; i++) {
            int sx = (int)(Math.random() * 800);
            int sy = (int)(Math.random() * 500);
            g.fillOval(sx, sy, 3, 3);
        }
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("SansSerif", Font.BOLD, 44));
        g.drawString("VICTORY!", 280, 160);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString("The Dark Castle has fallen!", 260, 210);
        g.drawString("Total Gems: " + (totalGemsEarned + combat.getGemsEarned()), 290, 270);
        g.drawString("Total Kills: " + (totalKills + player.getKills()), 300, 300);
        g.setColor(Color.YELLOW); g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Press ENTER to return", 300, 380);
    }

    public int getTotalGemsEarned() { return totalGemsEarned + (combat != null ? combat.getGemsEarned() : 0); }
    public int getTotalKills() { return totalKills + (player != null ? player.getKills() : 0); }
}
