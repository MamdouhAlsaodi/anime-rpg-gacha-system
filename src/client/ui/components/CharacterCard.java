package client.ui.components;

import server.model.abstracts.Character;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CharacterCard extends JPanel {
    private Character character;

    public CharacterCard(Character character) {
        this.character = character;
        setPreferredSize(new Dimension(150, 200));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(getRarityColor(), 2, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        Color bgColor = getRarityBgColor();
        setBackground(bgColor);

        JLabel nameLabel = new JLabel(character.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        add(nameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));
        centerPanel.setBackground(bgColor);
        JLabel elementLabel = new JLabel(character.getElement().getDisplayName(), SwingConstants.CENTER);
        elementLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        elementLabel.setForeground(Color.decode(character.getElement().getColor()));
        JLabel levelLabel = new JLabel("Lv." + character.getLevel(), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        levelLabel.setForeground(Color.LIGHT_GRAY);
        JLabel hpLabel = new JLabel("HP " + character.getHp(), SwingConstants.CENTER);
        hpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hpLabel.setForeground(Color.LIGHT_GRAY);
        JLabel atkLabel = new JLabel("ATK " + character.getAttack(), SwingConstants.CENTER);
        atkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        atkLabel.setForeground(Color.LIGHT_GRAY);
        JLabel defLabel = new JLabel("DEF " + character.getDefense(), SwingConstants.CENTER);
        defLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        defLabel.setForeground(Color.LIGHT_GRAY);
        centerPanel.add(elementLabel);
        centerPanel.add(levelLabel);
        centerPanel.add(hpLabel);
        centerPanel.add(atkLabel);
        centerPanel.add(defLabel);
        add(centerPanel, BorderLayout.CENTER);

        StarPanel starPanel = new StarPanel(character.getRarity());
        add(starPanel, BorderLayout.SOUTH);
    }

    private Color getRarityColor() {
        return switch (character.getRarity()) {
            case LEGENDARY -> new Color(255, 215, 0);
            case RARE -> new Color(79, 195, 247);
            case COMMON -> new Color(136, 136, 136);
        };
    }

    private Color getRarityBgColor() {
        return switch (character.getRarity()) {
            case LEGENDARY -> new Color(40, 35, 15);
            case RARE -> new Color(15, 30, 40);
            case COMMON -> new Color(25, 25, 30);
        };
    }

    public Character getCharacter() { return character; }
}
