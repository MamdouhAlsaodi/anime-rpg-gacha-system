package client.ui.animation;

import server.model.enums.Rarity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GachaAnimation extends JPanel {
    private List<Rarity> results;
    private int currentReveal;
    private Timer animationTimer;
    private Runnable onComplete;
    private List<Particle> particles;
    private float glowRadius;
    private boolean expanding;
    private Random random;

    public GachaAnimation() {
        setBackground(new Color(10, 10, 15));
        setPreferredSize(new Dimension(400, 300));
        this.particles = new ArrayList<>();
        this.random = new Random();
    }

    public void animateSummon(List<Object> summonedItems, Runnable onComplete) {
        this.results = new ArrayList<>();
        for (Object obj : summonedItems) {
            if (obj instanceof server.model.abstracts.Character c) {
                results.add(c.getRarity());
            } else if (obj instanceof server.model.abstracts.InventoryItem item) {
                results.add(item.getRarity());
            } else {
                results.add(Rarity.COMMON);
            }
        }
        this.currentReveal = 0;
        this.onComplete = onComplete;
        this.glowRadius = 0;
        this.expanding = true;

        animationTimer = new Timer(50, e -> animationStep());
        animationTimer.start();
    }

    private void animationStep() {
        if (expanding) {
            glowRadius += 8;
            if (glowRadius >= 150) {
                expanding = false;
            }
        } else {
            glowRadius -= 5;
            if (glowRadius <= 0) {
                glowRadius = 0;
                currentReveal++;
                if (currentReveal >= results.size()) {
                    animationTimer.stop();
                    if (onComplete != null) onComplete.run();
                    return;
                }
                expanding = true;
                glowRadius = 0;
            }
        }

        if (currentReveal < results.size() && random.nextFloat() < 0.3) {
            Rarity rarity = results.get(currentReveal);
            particles.add(new Particle(
                200 + random.nextInt(100) - 50,
                150 + random.nextInt(100) - 50,
                rarity
            ));
        }

        particles.removeIf(p -> p.life <= 0);
        for (Particle p : particles) {
            p.update();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        if (currentReveal < results.size()) {
            Rarity rarity = results.get(currentReveal);
            Color glowColor = switch (rarity) {
                case LEGENDARY -> new Color(255, 215, 0, 100);
                case RARE -> new Color(79, 195, 247, 80);
                case COMMON -> new Color(136, 136, 136, 40);
            };

            RadialGradientPaint glow = new RadialGradientPaint(
                cx, cy, Math.max(1, glowRadius),
                new float[]{0f, 1f},
                new Color[]{glowColor, new Color(0, 0, 0, 0)}
            );
            g2.setPaint(glow);
            g2.fill(new Ellipse2D.Double(cx - glowRadius, cy - glowRadius, glowRadius * 2, glowRadius * 2));

            for (Particle p : particles) {
                g2.setColor(p.getColor());
                g2.fillOval(p.x, p.y, p.size, p.size);
            }

            g2.setColor(switch (rarity) {
                case LEGENDARY -> new Color(255, 215, 0);
                case RARE -> new Color(79, 195, 247);
                case COMMON -> Color.LIGHT_GRAY;
            });
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            String text = rarity.toString();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, cx - fm.stringWidth(text) / 2, cy + 10);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString((currentReveal + 1) + "/" + results.size(), cx - 15, cy + 50);
        }
    }

    private static class Particle {
        int x, y, size;
        int vx, vy;
        int life;
        Rarity rarity;

        Particle(int x, int y, Rarity rarity) {
            this.x = x;
            this.y = y;
            this.rarity = rarity;
            this.size = 3 + (int)(Math.random() * 5);
            this.vx = (int)(Math.random() * 6 - 3);
            this.vy = (int)(Math.random() * 6 - 3);
            this.life = 20 + (int)(Math.random() * 20);
        }

        void update() {
            x += vx;
            y += vy;
            life--;
            size = Math.max(0, size - 1);
        }

        Color getColor() {
            return switch (rarity) {
                case LEGENDARY -> new Color(255, 215, 0, Math.min(255, life * 12));
                case RARE -> new Color(79, 195, 247, Math.min(255, life * 10));
                case COMMON -> new Color(200, 200, 200, Math.min(255, life * 8));
            };
        }
    }
}
