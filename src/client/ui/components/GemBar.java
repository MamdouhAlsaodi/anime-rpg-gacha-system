package client.ui.components;

import javax.swing.*;
import java.awt.*;

public class GemBar extends JPanel {
    private JLabel gemLabel;
    private int currentGems;
    private Timer flashTimer;

    public GemBar(int initialGems) {
        this.currentGems = initialGems;
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(20, 20, 30));
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(201, 168, 76)));

        JLabel iconLabel = new JLabel("\u2666");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconLabel.setForeground(new Color(79, 195, 247));

        gemLabel = new JLabel(String.format("%,d", currentGems));
        gemLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gemLabel.setForeground(new Color(201, 168, 76));

        add(iconLabel);
        add(gemLabel);

        flashTimer = new Timer(100, e -> {
            Color cur = gemLabel.getForeground();
            gemLabel.setForeground(cur.equals(Color.WHITE) ? new Color(201, 168, 76) : Color.WHITE);
        });
        flashTimer.setRepeats(true);
    }

    public void updateGems(int amount) {
        this.currentGems = amount;
        gemLabel.setText(String.format("%,d", amount));
        flashTimer.start();
        Timer stopFlash = new Timer(600, e -> {
            flashTimer.stop();
            gemLabel.setForeground(new Color(201, 168, 76));
        });
        stopFlash.setRepeats(false);
        stopFlash.start();
    }

    public int getCurrentGems() { return currentGems; }
}
