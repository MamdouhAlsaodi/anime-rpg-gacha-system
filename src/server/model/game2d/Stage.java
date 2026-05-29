package server.model.game2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Stage {
    public enum StageTheme { FOREST, DESERT, CAVE, VOLCANO, CASTLE }

    private int stageNumber;
    private String name;
    private StageTheme theme;
    private Color bgColor;
    private Color groundColor;
    private int totalWaves;
    private int enemiesPerWave;
    private boolean hasBoss;
    private String bossName;
    private int bossHP;
    private int bossAttack;
    private int enemyBaseHP;
    private int enemyBaseAttack;
    private int enemySpeed;
    private int rewardGems;

    public Stage(int number, String name, StageTheme theme, Color bg, Color ground,
                 int waves, int enemiesPerWave, boolean hasBoss,
                 int eHP, int eAtk, int eSpd, int gems) {
        this.stageNumber = number;
        this.name = name;
        this.theme = theme;
        this.bgColor = bg;
        this.groundColor = ground;
        this.totalWaves = waves;
        this.enemiesPerWave = enemiesPerWave;
        this.hasBoss = hasBoss;
        this.enemyBaseHP = eHP;
        this.enemyBaseAttack = eAtk;
        this.enemySpeed = eSpd;
        this.rewardGems = gems;
        this.bossName = "";
        this.bossHP = 0;
        this.bossAttack = 0;
    }

    public static Stage createStage(int number) {
        return switch (number) {
            case 1 -> new Stage(1, "The Enchanted Forest", StageTheme.FOREST,
                new Color(20, 60, 20), new Color(34, 100, 34),
                3, 4, false, 50, 8, 1, 500);
            case 2 -> new Stage(2, "Scorching Desert", StageTheme.DESERT,
                new Color(80, 60, 30), new Color(194, 154, 80),
                4, 5, false, 80, 12, 2, 1000);
            case 3 -> new Stage(3, "Dark Caverns", StageTheme.CAVE,
                new Color(25, 20, 35), new Color(60, 50, 70),
                4, 6, false, 120, 16, 2, 1500);
            case 4 -> new Stage(4, "Volcanic Wasteland", StageTheme.VOLCANO,
                new Color(60, 15, 10), new Color(80, 30, 20),
                5, 7, false, 180, 22, 3, 2500);
            case 5 -> new Stage(5, "The Dark Castle", StageTheme.CASTLE,
                new Color(15, 10, 25), new Color(50, 40, 60),
                5, 8, true, 250, 30, 3, 5000);
            default -> new Stage(1, "Unknown", StageTheme.FOREST,
                Color.BLACK, Color.GRAY, 1, 1, false, 10, 5, 1, 100);
        };
    }

    public void setBoss(String name, int hp, int atk) {
        this.bossName = name; this.bossHP = hp; this.bossAttack = atk; this.hasBoss = true;
    }

    public int getStageNumber() { return stageNumber; }
    public String getName() { return name; }
    public StageTheme getTheme() { return theme; }
    public Color getBgColor() { return bgColor; }
    public Color getGroundColor() { return groundColor; }
    public int getTotalWaves() { return totalWaves; }
    public int getEnemiesPerWave() { return enemiesPerWave; }
    public boolean hasBoss() { return hasBoss; }
    public String getBossName() { return bossName; }
    public int getBossHP() { return bossHP; }
    public int getBossAttack() { return bossAttack; }
    public int getEnemyBaseHP() { return enemyBaseHP; }
    public int getEnemyBaseAttack() { return enemyBaseAttack; }
    public int getEnemySpeed() { return enemySpeed; }
    public int getRewardGems() { return rewardGems; }
}
