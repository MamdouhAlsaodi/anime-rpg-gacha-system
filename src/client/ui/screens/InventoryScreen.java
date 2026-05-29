package client.ui.screens;

import client.network.ServerConnector;
import api.protocol.GameResponse;

import javax.swing.*;
import java.awt.*;

public class InventoryScreen extends JPanel {
    private ServerConnector connector;

    public InventoryScreen(ServerConnector connector) {
        this.connector = connector;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(10, 10, 15));

        JLabel title = new JLabel("Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(201, 168, 76));
        add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        filterPanel.setBackground(new Color(15, 15, 25));
        String[] filters = {"All", "Characters", "Weapons", "Artifacts", "Relics"};
        for (String filter : filters) {
            JButton btn = new JButton(filter);
            btn.setBackground(new Color(30, 30, 45));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            filterPanel.add(btn);
        }
        add(filterPanel, BorderLayout.BEFORE_FIRST_LINE);

        JPanel contentPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        contentPanel.setBackground(new Color(10, 10, 15));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(201, 168, 76));
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.addActionListener(e -> refresh(contentPanel));
        add(refreshBtn, BorderLayout.SOUTH);

        refresh(contentPanel);
    }

    private void refresh(JPanel contentPanel) {
        contentPanel.removeAll();
        GameResponse response = connector.viewInventory();
        if (response.isSuccess()) {
            JLabel summary = new JLabel("<html><pre style='color:white'>" + response.getMessage() + "</pre></html>");
            contentPanel.add(summary);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
