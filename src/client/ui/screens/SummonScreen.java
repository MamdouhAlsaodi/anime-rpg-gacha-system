package client.ui.screens;

import api.protocol.GameResponse;
import client.network.ServerConnector;

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(new Color(10, 10, 15));

        JButton summonOne = createSummonButton("Summon x1", "160 Gems");
        JButton summonTenBtn = createSummonButton("Summon x10", "1600 Gems");

        buttonPanel.add(summonOne);
        buttonPanel.add(summonTenBtn);
        add(buttonPanel, BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(10, 10, 15));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        add(scrollPane, BorderLayout.SOUTH);

        summonOne.addActionListener(e -> performSummon(1, resultsPanel));
        summonTenBtn.addActionListener(e -> performSummon(10, resultsPanel));
    }

    private void performSummon(int count, JPanel resultsPanel) {
        GameResponse response = count == 1 ? connector.summonSingle() : connector.summonTen();
        if (response.isSuccess()) {
            resultsPanel.removeAll();
            if (response.getData() instanceof List<?> results) {
                for (Object obj : results) {
                    JLabel label = new JLabel("  " + obj.toString());
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    resultsPanel.add(label);
                }
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
            refreshGems.run();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
