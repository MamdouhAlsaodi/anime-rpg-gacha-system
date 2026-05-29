package server.model.game2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CombatEngine {
    private PlayerEntity player;
    private List<Enemy2D> enemies;
    private Stage stage;
    private int currentWave;
    private int spawnTimer;
    private int gemsEarned;
    private Random rand;
    private boolean waveCleared;
    private boolean stageComplete;
    private String combatLog;
    private int logTimer;

    public CombatEngine(PlayerEntity player, Stage stage) {
        this.player = player;
        this.stage = stage;
        this.enemies = new ArrayList<>();
        this.currentWave = 1;
        this.spawnTimer = 0;
        this.gemsEarned = 0;
        this.rand = new Random();
        this.waveCleared = false;
        this.stageComplete = false;
        this.combatLog = "";
        this.logTimer = 0;
    }

    public void spawnWave(int panelW, int panelH) {
        int count = stage.getEnemiesPerWave();
        boolean isFinalWave = (currentWave == stage.getTotalWaves());

        for (int i = 0; i < count; i++) {
            String[] names = {"Goblin","Skeleton","Slime","Orc","Wraith","Shadow","Imp","Troll","Demon","Banshee"};
            String name = names[rand.nextInt(names.length)];
            int ex = rand.nextInt(panelW - 100) + 50;
            int ey = rand.nextInt(panelH / 2) + panelH / 4;
            Enemy2D e = new Enemy2D(name, ex, ey,
                stage.getEnemyBaseHP() + rand.nextInt(20),
                stage.getEnemyBaseAttack() + rand.nextInt(5),
                stage.getEnemySpeed(), false);
            enemies.add(e);
        }

        // Boss on final wave
        if (isFinalWave && stage.hasBoss()) {
            stage.setBoss("Dark Lord", stage.getBossHP() > 0 ? stage.getBossHP() : 500, stage.getBossAttack() > 0 ? stage.getBossAttack() : 40);
            Enemy2D boss = new Enemy2D(stage.getBossName(), panelW/2, 100,
                stage.getBossHP() > 0 ? stage.getBossHP() : 500,
                stage.getBossAttack() > 0 ? stage.getBossAttack() : 40, 2, true);
            boss.setSpeed(2);
            boss.setColor(new Color(180, 0, 180));
            enemies.add(boss);
            setLog("BOSS: " + stage.getBossName() + " appears!");
        }

        waveCleared = false;
    }

    public void update(int panelW, int panelH) {
        if (stageComplete) return;
        if (logTimer > 0) logTimer--;

        // Spawn first wave
        if (enemies.isEmpty() && !waveCleared) {
            spawnWave(panelW, panelH);
        }

        // Move enemies toward player
        for (Enemy2D e : enemies) {
            if (e.isAlive()) {
                e.moveToward(player.getX(), player.getY());
                // Enemy attacks player if close
                if (e.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(e.getAttack());
                }
            }
        }

        // Player attacks enemies
        if (player.isAlive() && player.getAttackBounds() != null) {
            for (Enemy2D e : enemies) {
                if (e.isAlive() && player.getAttackBounds().intersects(e.getBounds())) {
                    int dmg = player.getTotalAttack() + rand.nextInt(10);
                    e.takeDamage(dmg);
                    if (!e.isAlive()) {
                        player.addKill();
                        int gems = e.isBoss() ? 200 : 10 + rand.nextInt(20);
                        gemsEarned += gems;
                    }
                }
            }
        }

        // Remove dead enemies
        enemies.removeIf(e -> !e.isAlive());

        // Check wave clear
        if (enemies.isEmpty() && !waveCleared) {
            waveCleared = true;
            if (currentWave >= stage.getTotalWaves()) {
                stageComplete = true;
                gemsEarned += stage.getRewardGems();
                setLog("STAGE " + stage.getStageNumber() + " COMPLETE! +" + stage.getRewardGems() + " Gems!");
            } else {
                currentWave++;
                setLog("Wave " + (currentWave-1) + " cleared! Wave " + currentWave + " incoming...");
                waveCleared = false;
            }
        }

        player.update();
    }

    public boolean isStageComplete() { return stageComplete; }
    public boolean isPlayerDead() { return !player.isAlive(); }
    public List<Enemy2D> getEnemies() { return enemies; }
    public int getGemsEarned() { return gemsEarned; }
    public int getCurrentWave() { return currentWave; }
    public Stage getStage() { return stage; }
    public String getCombatLog() { return combatLog; }
    public int getLogTimer() { return logTimer; }
    public void setLog(String msg) { combatLog = msg; logTimer = 120; }
}