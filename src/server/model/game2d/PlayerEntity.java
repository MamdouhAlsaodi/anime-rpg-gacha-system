package server.model.game2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
import server.model.abstracts.Character;
import server.model.abstracts.InventoryItem;
import server.model.items.Artifact;
import server.model.enums.Rarity;

public class PlayerEntity {
    private String charName;
    private int x, y;
    private int width = 32, height = 40;
    private int hp, maxHP;
    private int baseAttack;
    private int weaponBonus;
    private int artifactBonus;
    private int defense;
    private Rarity charRarity;
    private String weaponName;
    private String artifactName;
    private boolean attacking;
    private int attackTimer;
    private int invincible;
    private int animFrame;
    private int facing; // -1 left, 1 right
    private int totalKills;
    private int damageFlash;

    public PlayerEntity(Character character, InventoryItem weapon) {
        this(character, weapon, null);
    }

    public PlayerEntity(Character character, InventoryItem weapon, Artifact artifact) {
        this.charName = character.getName();
        this.charRarity = character.getRarity();
        this.baseAttack = character.getLevel() / 2 + 5;
        this.defense = character.getLevel() / 5;
        this.hp = 100 + character.getLevel() * 5;
        this.maxHP = hp;
        this.weaponBonus = 0;
        this.artifactBonus = 0;
        this.weaponName = "Fists";
        this.artifactName = "No Artifact";
        if (weapon != null) {
            this.weaponName = weapon.getName();
            this.weaponBonus = weapon.getLevel() * 3 + (weapon.getRarity() == Rarity.LEGENDARY ? 20 : weapon.getRarity() == Rarity.RARE ? 10 : 3);
        }
        if (artifact != null) {
            this.artifactName = artifact.getName();
            this.artifactBonus = artifact.getStatBonus();
            this.maxHP += artifactBonus * 2;
            this.hp = maxHP;
            this.defense += Math.max(1, artifactBonus / 5);
        }
        this.attacking = false;
        this.attackTimer = 0;
        this.invincible = 0;
        this.animFrame = 0;
        this.facing = 1;
        this.totalKills = 0;
        this.damageFlash = 0;
    }

    public void move(int dx, int dy, int panelW, int panelH, int groundY) {
        x += dx; y += dy;
        if (x < width/2) x = width/2;
        if (x > panelW - width/2) x = panelW - width/2;
        if (y < groundY - 20) y = groundY - 20;
        if (y > panelH - height/2) y = panelH - height/2;
        if (dx != 0) facing = dx > 0 ? 1 : -1;
        animFrame = (animFrame + 1) % 30;
    }

    public void attack() {
        if (!attacking) { attacking = true; attackTimer = 12; }
    }

    public int getTotalAttack() { return baseAttack + weaponBonus + Math.max(0, artifactBonus / 2); }

    public Rectangle getAttackBounds() {
        int aw = 40; int ah = 30;
        int ax = facing == 1 ? x + width/2 : x - width/2 - aw;
        return new Rectangle(ax, y - ah/2, aw, ah);
    }

    public void takeDamage(int dmg) {
        if (invincible > 0) return;
        int actual = Math.max(1, dmg - defense);
        hp -= actual;
        invincible = 30;
        damageFlash = 10;
        if (hp < 0) hp = 0;
    }

    public void update() {
        if (attackTimer > 0) { attackTimer--; if (attackTimer == 0) attacking = false; }
        if (invincible > 0) invincible--;
        if (damageFlash > 0) damageFlash--;
    }

    public void draw(Graphics g) {
        Color bodyColor = (damageFlash > 0) ? Color.RED :
            switch (charRarity) {
                case LEGENDARY -> new Color(255, 180, 0);
                case RARE -> new Color(80, 140, 255);
                default -> new Color(100, 220, 100);
            };

        // Body
        g.setColor(bodyColor);
        g.fillRect(x - width/2, y - height/2, width, height);

        // Head
        g.setColor(new Color(255, 220, 180));
        g.fillOval(x - 10, y - height/2 - 14, 20, 18);

        // Eyes
        g.setColor(Color.BLACK);
        int ex = facing == 1 ? 3 : -3;
        g.fillOval(x - 4 + ex, y - height/2 - 8, 4, 4);
        g.fillOval(x + 2 + ex, y - height/2 - 8, 4, 4);

        // Weapon
        if (attacking) {
            g.setColor(Color.YELLOW);
            int wx = facing == 1 ? x + width/2 : x - width/2 - 20;
            g.fillRect(wx, y - 15, 20, 6);
            g.setColor(Color.WHITE);
            g.drawString("*", wx + (facing == 1 ? 18 : 2), y - 5);
        }

        // HP bar
        int barW = 44;
        g.setColor(Color.RED);
        g.fillRect(x - barW/2, y - height/2 - 22, barW, 4);
        g.setColor(Color.GREEN);
        g.fillRect(x - barW/2, y - height/2 - 22, (int)(barW * (double)hp / maxHP), 4);

        // Name + weapon
        g.setColor(Color.WHITE);
        g.drawString(charName, x - charName.length() * 3, y - height/2 - 26);
        g.setColor(Color.GRAY);
        g.drawString(weaponName, x - weaponName.length() * 3, y + height/2 + 14);
        if (!"No Artifact".equals(artifactName)) {
            g.setColor(new Color(201, 168, 76));
            g.drawString("Art: " + artifactName, x - Math.min(artifactName.length() * 3, 45), y + height/2 + 28);
        }
    }

    public Rectangle getBounds() { return new Rectangle(x - width/2, y - height/2, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }
    public boolean isAlive() { return hp > 0; }
    public int getKills() { return totalKills; }
    public void addKill() { totalKills++; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void heal(int amount) { hp = Math.min(maxHP, hp + amount); }
    public String getCharName() { return charName; }
}
