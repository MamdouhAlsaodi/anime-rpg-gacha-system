package client.ui.components;

import server.model.enums.Rarity;

import javax.swing.*;
import java.awt.*;

public class StarPanel extends JPanel {
    private Rarity rarity;

    public StarPanel(Rarity rarity) {
        this.rarity = rarity;
        setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        setBackground(new Color(0, 0, 0, 0));

        int stars = switch (rarity) {
            case LEGENDARY -> 5;
            case RARE -> 3;
            case COMMON -> 1;
        };

        Color starColor = switch (rarity) {
            case LEGENDARY -> new Color(255, 215, 0);
            case RARE -> new Color(79, 195, 247);
            case COMMON -> new Color(136, 136, 136);
        };

        for (int i = 0; i < stars; i++) {
            JLabel star = new JLabel("\u2605");
            star.setFont(new Font("Segoe UI", Font.BOLD, 14));
            star.setForeground(starColor);
            add(star);
        }
    }

    public Rarity getRarity() { return rarity; }
}
