package server.model.game2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Enemy2D {
    private String name;
    private int x, y;
    private int width, height;
    private int hp, maxHP;
    private int attack;
    private int speed;
    private boolean isBoss;
    private Color color;
    private boolean alive;
    private int animFrame;
    private int damageFlash;

    public Enemy2D(String name, int x, int y, int hp, int attack, int speed, boolean isBoss) {
        this.name = name;
        this.x = x; this.y = y;
        this.hp = hp; this.maxHP = hp;
        this.attack = attack; this.speed = speed;
        this.isBoss = isBoss;
        this.alive = true; this.animFrame = 0; this.damageFlash = 0;
        this.width = isBoss ? 60 : 36;
        this.height = isBoss ? 60 : 36;
        this.color = isBoss ? new Color(180, 0, 180) : new Color(200, 50, 50);
    }

    public void moveToward(int targetX, int targetY) {
        if (!alive) return;
        int dx = targetX - x;
        int dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 5) {
            x += (int)(dx / dist * speed);
            y += (int)(dy / dist * speed);
        }
        animFrame = (animFrame + 1) % 20;
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        damageFlash = 6;
        if (hp <= 0) { hp = 0; alive = false; }
    }

    public void draw(Graphics g) {
        if (!alive) return;
        Color drawColor = (damageFlash > 0) ? Color.WHITE : color;
        if (damageFlash > 0) damageFlash--;

        g.setColor(drawColor);
        g.fillRect(x - width/2, y - height/2, width, height);

        // Eyes
        g.setColor(Color.YELLOW);
        int eyeSize = isBoss ? 8 : 5;
        g.fillRect(x - width/4, y - height/4, eyeSize, eyeSize);
        g.fillRect(x + width/4 - eyeSize, y - height/4, eyeSize, eyeSize);

        // HP bar
        int barW = width + 10;
        int barH = 4;
        int barX = x - barW/2;
        int barY = y - height/2 - 10;
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barW, barH);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, (int)(barW * (double)hp / maxHP), barH);

        // Name
        g.setColor(Color.WHITE);
        g.drawString(name, x - name.length() * 3, y - height/2 - 14);

        // Boss crown
        if (isBoss) {
            g.setColor(Color.YELLOW);
            int[] cx = {x-12, x-8, x-4, x, x+4, x+8, x+12};
            int[] cy = {y-height/2-4, y-height/2-12, y-height/2-4, y-height/2-14, y-height/2-4, y-height/2-12, y-height/2-4};
            g.fillPolygon(cx, cy, 7);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x - width/2, y - height/2, width, height);
    }

    public boolean isAlive() { return alive; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAttack() { return attack; }
    public int getHP() { return hp; }
    public int getMaxHP() { return maxHP; }
    public boolean isBoss() { return isBoss; }
    public String getName() { return name; }
    public void setSpeed(int s) { speed = s; }
    public void setColor(Color c) { color = c; }
}
