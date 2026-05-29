package client.ui.screens;

import api.protocol.GameResponse;
import client.network.ServerConnector;
import client.ui.components.CharacterCard;
import client.ui.components.ItemCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SummonScreen extends JPanel {
    private ServerConnector connector;
    private Runnable refreshGems;

    public SummonScreen(ServerConnector connector, Runnable refreshGems) {
        this.connector = connector;
        this.refreshGems = refreshGems;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(10, 10, 15));

        JLabel title = new JLabel("Summon Portal", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(201, 168, 76));
        add(title, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(10, 10, 15));
        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(new Color(10, 10, 15));

        JButton summonOne = createSummonButton("Summon x1", "160 Gems");
        JButton summonTenBtn = createSummonButton("Summon x10", "1600 Gems");

        buttonPanel.add(summonOne);
        buttonPanel.add(summonTenBtn);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(10, 10, 15));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel hint = new JLabel("Pull characters, weapons, artifacts, and relics here.", SwingConstants.CENTER);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hint.setForeground(new Color(136, 136, 136));
        resultsPanel.add(hint);

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBackground(new Color(10, 10, 15));
        scrollPane.getViewport().setBackground(new Color(10, 10, 15));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 45)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        summonOne.addActionListener(e -> performSummon(1, resultsPanel));
        summonTenBtn.addActionListener(e -> performSummon(10, resultsPanel));
    }

    private void performSummon(int count, JPanel resultsPanel) {
        GameResponse response = count == 1 ? connector.summonSingle() : connector.summonTen();
        if (response.isSuccess()) {
            resultsPanel.removeAll();
            JLabel banner = new JLabel(count == 1 ? "Summon Result" : "Summon x10 Results", SwingConstants.CENTER);
            banner.setAlignmentX(Component.LEFT_ALIGNMENT);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 18));
            banner.setForeground(new Color(201, 168, 76));
            resultsPanel.add(banner);

            if (response.getData() instanceof List<?> results) {
                JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
                cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardsPanel.setBackground(new Color(10, 10, 15));
                for (Object obj : results) {
                    if (obj instanceof server.model.abstracts.Character character) {
                        cardsPanel.add(new CharacterCard(character));
                    } else if (obj instanceof server.model.abstracts.InventoryItem item) {
                        cardsPanel.add(new ItemCard(item));
                    } else {
                        cardsPanel.add(createFallbackResult(obj));
                    }
                }
                resultsPanel.add(cardsPanel);
            } else if (response.getData() != null) {
                resultsPanel.add(createFallbackResult(response.getData()));
            } else {
                resultsPanel.add(createFallbackResult(response.getMessage()));
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
            refreshGems.run();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JComponent createFallbackResult(Object obj) {
        JLabel label = new JLabel("  " + String.valueOf(obj));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return label;
    }

    private JButton createSummonButton(String text, String subtitle) {
        JButton btn = new JButton("<html><center>" + text + "<br><small>" + subtitle + "</small></center></html>");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(new Color(201, 168, 76));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 80));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
