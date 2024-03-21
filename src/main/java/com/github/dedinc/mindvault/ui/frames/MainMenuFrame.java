package com.github.dedinc.mindvault.ui.frames;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.dedinc.mindvault.core.PomodoroTimer;
import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.ui.UIComponents;
import com.github.dedinc.mindvault.ui.UITheme;
import com.github.dedinc.mindvault.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainMenuFrame extends JFrame {
    private JButton createSessionButton;
    private JButton loadSessionButton;
    private JButton saveSessionButton;
    private JButton startSessionButton;
    private JButton showForgettingGraphButton;
    private JButton manageCardsButton;
    private JComboBox<String> themeComboBox;
    private Session session;
    SessionFrame sessionFrame;

    private Timer pomodoroTimer;
    private int remainingTime;
    private JLabel pomodoroStatusLabel;

    private JLabel pomodoroInfoLabel;

    public MainMenuFrame() {
        FlatLightLaf.setup();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("MindVault - Main Menu");
        setPreferredSize(new Dimension(600, 500));

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel sessionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        createSessionButton = UIComponents.createButtonWithEvent("Create Session", e -> createSession());
        sessionPanel.add(createSessionButton, gbc);

        loadSessionButton = UIComponents.createButtonWithEvent("Load Session", e -> loadSession());
        sessionPanel.add(loadSessionButton, gbc);

        saveSessionButton = UIComponents.createButtonWithEvent("Save Session", e -> {
            File sessionFile = FileUtils.showSaveSessionDialog(this);
            if (sessionFile != null) {
                FileUtils.saveSession(session, sessionFile);
            }
        });
        saveSessionButton.setEnabled(false);
        sessionPanel.add(saveSessionButton, gbc);

        startSessionButton = UIComponents.createButtonWithEvent("Start Session", e -> openSessionFrame());
        startSessionButton.setEnabled(false);
        sessionPanel.add(startSessionButton, gbc);

        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.add(new JLabel("Theme:"));
        themeComboBox = UITheme.createThemeComboBox();
        String[] previousSelectedItem = {"Arc"};
        themeComboBox.addActionListener(e -> {
            String selectedItem = (String) themeComboBox.getSelectedItem();
            if (selectedItem != null && (selectedItem.contains("Select Theme") ||
                    selectedItem.contains("Light Themes") || selectedItem.contains("Dark Themes"))) {
                String prevSelection = previousSelectedItem[0];
                themeComboBox.setSelectedItem(previousSelectedItem[0]);
                if (prevSelection.equals(previousSelectedItem[0])) {
                    return;
                }
            } else {
                previousSelectedItem[0] = selectedItem;
            }
            UITheme.changeTheme();
        });

        themePanel.add(themeComboBox);
        sessionPanel.add(themePanel, gbc);

        JPanel pomodoroPanel = new JPanel(new GridBagLayout());
        pomodoroStatusLabel = UIComponents.createLabel("");
        pomodoroPanel.add(pomodoroStatusLabel, gbc);
        pomodoroInfoLabel = UIComponents.createLabel("");
        pomodoroPanel.add(pomodoroInfoLabel, gbc);
        sessionPanel.add(pomodoroPanel, gbc);

        tabbedPane.addTab("Session", sessionPanel);

        JPanel cardsPanel = new JPanel(new GridBagLayout());

        manageCardsButton = UIComponents.createButtonWithEvent("Manage Cards", e -> openCardManagementFrame());
        manageCardsButton.setEnabled(false);
        cardsPanel.add(manageCardsButton, gbc);

        showForgettingGraphButton = UIComponents.createButtonWithEvent("Show Forgetting Graph", e -> openForgettingGraphFrame());
        showForgettingGraphButton.setEnabled(false);
        cardsPanel.add(showForgettingGraphButton, gbc);

        tabbedPane.addTab("Cards/Graph", cardsPanel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        pack();
        UITheme.setTheme(UITheme.getThemeCache().get("Arc"));
    }

    private void startPomodoroTimer() {
        remainingTime = session.getPomodoroTimer().getCurrentInterval() * 60;
        pomodoroTimer = new Timer(1000, e -> {
            remainingTime--;
            updatePomodoroStatus();

            if (remainingTime <= 0) {
                pomodoroTimer.stop();
                session.getPomodoroTimer().nextInterval();
                startPomodoroTimer();
            }
        });
        pomodoroTimer.start();
    }

    private void updatePomodoroStatus() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;

        String status;
        if (session.getPomodoroTimer().isBreakInterval()) {
            status = String.format("Break Time: %02d:%02d", minutes, seconds);
        } else {
            status = String.format("Pomodoro: %02d:%02d", minutes, seconds);
        }
        pomodoroStatusLabel.setText(status);

        String info;
        if (session.getPomodoroTimer().isBreakInterval()) {
            info = "Take a short break and relax.";
        } else {
            int completedBlocks = session.getPomodoroTimer().getCompletedBlocks();
            int remainingBlocks = PomodoroTimer.MAX_BLOCKS_PER_DAY - completedBlocks;
            info = String.format("Completed Blocks: %d, Remaining Blocks: %d", completedBlocks, remainingBlocks);
        }
        pomodoroInfoLabel.setText(info);

        startSessionButton.setEnabled(session.getPomodoroTimer().canStartSession());
    }

    private void createSession() {
        session = FileUtils.createNewSession();
        manageCardsButton.setEnabled(true);
        startSessionButton.setEnabled(true);
        saveSessionButton.setEnabled(true);
        showForgettingGraphButton.setEnabled(true);
        startPomodoroTimer();
    }

    private void openForgettingGraphFrame() {
        ForgettingGraphFrame forgettingGraphFrame = new ForgettingGraphFrame(session);
        forgettingGraphFrame.setVisible(true);
    }

    private void loadSession() {
        File selectedFile = FileUtils.showLoadSessionDialog(this);
        if (selectedFile != null) {
            session = FileUtils.loadSession(selectedFile);
            session.updateCards();
            manageCardsButton.setEnabled(true);
            startSessionButton.setEnabled(true);
            saveSessionButton.setEnabled(true);
            showForgettingGraphButton.setEnabled(true);
            startPomodoroTimer();
        }
    }

    private void openSessionFrame() {
        if (sessionFrame == null) {
            sessionFrame = new SessionFrame(session);
        }
        sessionFrame.setVisible(true);
    }

    private void openCardManagementFrame() {
        CardManagementFrame cardManagementFrame = new CardManagementFrame(session);
        cardManagementFrame.setVisible(true);
    }
}