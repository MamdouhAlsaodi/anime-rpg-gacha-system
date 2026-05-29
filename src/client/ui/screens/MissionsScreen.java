package client.ui.screens;

import client.network.ServerConnector;
import server.model.player.Player;
import server.service.ResourceSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MissionsScreen extends JPanel {
    private final ServerConnector connector;
    private final Runnable refreshGems;
    private JPanel missionList;
    private JLabel currencyLabel;
    private JLabel statusLabel;

    private static final Color BG = new Color(10, 10, 15);
    private static final Color PANEL = new Color(20, 20, 30);
    private static final Color PANEL_2 = new Color(30, 30, 45);
    private static final Color GOLD = new Color(201, 168, 76);

    public MissionsScreen(ServerConnector connector, Runnable refreshGems) {
        this.connector = connector;
        this.refreshGems = refreshGems;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        setBackground(BG);
        buildHeader();
        buildMissionList();
        buildFooter();
        refresh();
    }

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 8));
        header.setBackground(BG);

        JLabel title = new JLabel("Missions & Quest Board", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(GOLD);
        header.add(title, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 0));
        actions.setBackground(BG);
        actions.add(button("Daily Login", () -> runClaim(() -> engine().claimDailyLogin())));
        actions.add(button("Beginner Pack", () -> runClaim(() -> engine().claimQuestReward("beginner"))));
        actions.add(button("Adventurer Pack", () -> runClaim(() -> engine().claimQuestReward("adventurer"))));
        actions.add(button("Veteran Pack", () -> runClaim(() -> engine().claimQuestReward("veteran"))));
        actions.add(button("Refresh", this::refresh));
        header.add(actions, BorderLayout.CENTER);

        currencyLabel = new JLabel(" ", SwingConstants.CENTER);
        currencyLabel.setForeground(Color.WHITE);
        currencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(currencyLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setBackground(PANEL_2);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        b.addActionListener(e -> action.run());
        return b;
    }

    private void buildMissionList() {
        missionList = new JPanel(new GridLayout(0, 2, 10, 10));
        missionList.setBackground(BG);
        JScrollPane scrollPane = new JScrollPane(missionList);
        scrollPane.setBorder(BorderFactory.createLineBorder(PANEL_2));
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void buildFooter() {
        statusLabel = new JLabel(" Complete missions to earn Gems plus C/R upgrade materials.");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(PANEL);
        statusLabel.setForeground(new Color(210, 210, 210));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private server.engine.GameEngine engine() {
        return connector.getOfflineEngine();
    }

    private void runClaim(ClaimAction action) {
        if (!connector.isOfflineMode()) {
            statusLabel.setText(" Mission board works in offline demo mode.");
            return;
        }
        statusLabel.setText(" " + action.claim());
        refreshGems.run();
        refresh();
    }

    private void refresh() {
        if (!connector.isOfflineMode() || engine() == null) return;
        Player player = engine().getPlayer();
        currencyLabel.setText(String.format("Gems: %,d    |    C: %,d    |    R: %,d", player.getGems(), player.getCommonCurrency(), player.getRareCurrency()));

        missionList.removeAll();
        ResourceSystem resources = engine().getResourceSystem();
        for (Map.Entry<String, ResourceSystem.Mission> entry : resources.getDailyMissions().entrySet()) {
            missionList.add(missionCard(entry.getValue()));
        }
        missionList.revalidate();
        missionList.repaint();
    }

    private JPanel missionCard(ResourceSystem.Mission mission) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(mission.completed ? new Color(26, 45, 32) : PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mission.completed ? new Color(92, 180, 105) : GOLD, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel title = new JLabel((mission.completed ? "✓ " : "• ") + mission.name);
        title.setForeground(mission.completed ? new Color(150, 230, 160) : GOLD);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        card.add(title, BorderLayout.NORTH);

        JLabel body = new JLabel("<html><span style='color:white'>" + mission.description + "</span><br>" +
            "<span style='color:#C9A84C'>Progress: " + Math.min(mission.progress, mission.required) + "/" + mission.required +
            " | Reward: +" + mission.reward + " Gems + C/R</span></html>");
        body.setForeground(Color.WHITE);
        card.add(body, BorderLayout.CENTER);

        JProgressBar bar = new JProgressBar(0, Math.max(1, mission.required));
        bar.setValue(Math.min(mission.progress, mission.required));
        bar.setStringPainted(true);
        bar.setForeground(GOLD);
        bar.setBackground(new Color(45, 45, 58));
        card.add(bar, BorderLayout.SOUTH);
        return card;
    }

    private interface ClaimAction { String claim(); }
}
