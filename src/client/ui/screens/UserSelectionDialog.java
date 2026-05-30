package client.ui.screens;

import client.profile.UserProfileManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserSelectionDialog extends JDialog {
    private static final Color BG = new Color(10, 10, 15);
    private static final Color PANEL = new Color(20, 20, 30);
    private static final Color GOLD = new Color(201, 168, 76);
    private static final Color TAB = new Color(30, 30, 45);

    private final UserProfileManager manager;
    private JComboBox<String> userBox;
    private String selectedUsername;

    public UserSelectionDialog(Frame owner, UserProfileManager manager) {
        super(owner, "Anime RPG Gacha - Choose User", true);
        this.manager = manager;
        initUI();
    }

    private void initUI() {
        setSize(430, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel("Choose Player Profile", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(18, 10, 4, 10));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));
        center.setBackground(PANEL);
        center.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel hint = new JLabel("Select an existing user, or add a second player.", SwingConstants.CENTER);
        hint.setForeground(Color.LIGHT_GRAY);
        center.add(hint);

        userBox = new JComboBox<>();
        userBox.setBackground(TAB);
        userBox.setForeground(Color.WHITE);
        refreshUsers();
        center.add(userBox);

        JLabel fileHint = new JLabel("Profiles: " + manager.getUsersFile(), SwingConstants.CENTER);
        fileHint.setForeground(new Color(160, 160, 160));
        fileHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        center.add(fileHint);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setBackground(BG);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));

        JButton start = goldButton("Start");
        start.addActionListener(e -> startSelectedUser());
        JButton add = darkButton("Add User");
        add.addActionListener(e -> addUser());
        JButton cancel = darkButton("Cancel");
        cancel.addActionListener(e -> {
            selectedUsername = null;
            dispose();
        });

        buttons.add(start);
        buttons.add(add);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);
    }

    private JButton goldButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(GOLD);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        return b;
    }

    private JButton darkButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(TAB);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        return b;
    }

    private void refreshUsers() {
        userBox.removeAllItems();
        List<String> users = manager.readUsers();
        for (String user : users) userBox.addItem(user);
    }

    private void startSelectedUser() {
        Object selected = userBox.getSelectedItem();
        if (selected == null) {
            showError("Please add or select a user first.");
            return;
        }
        selectedUsername = selected.toString();
        dispose();
    }

    private void addUser() {
        String name = JOptionPane.showInputDialog(this,
            "Enter new username:",
            "Add User", JOptionPane.QUESTION_MESSAGE);
        if (name == null) return;
        try {
            String added = manager.addUser(name);
            refreshUsers();
            userBox.setSelectedItem(added);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Profile Error", JOptionPane.WARNING_MESSAGE);
    }

    public String getSelectedUsername() {
        return selectedUsername;
    }

    public static String chooseUser() {
        UserSelectionDialog dialog = new UserSelectionDialog(null, new UserProfileManager());
        dialog.setVisible(true);
        return dialog.getSelectedUsername();
    }
}
