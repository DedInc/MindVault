package com.github.dedinc.mindvault.ui.frames;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.dedinc.mindvault.core.Session;
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

    public MainMenuFrame() {
        FlatLightLaf.setup();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("MindVault - Main Menu");
        setPreferredSize(new Dimension(530, 400));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        createSessionButton = new JButton("Create Session");
        createSessionButton.addActionListener(e -> createSession());
        mainPanel.add(createSessionButton, gbc);

        loadSessionButton = new JButton("Load Session");
        loadSessionButton.addActionListener(e -> loadSession());
        mainPanel.add(loadSessionButton, gbc);

        saveSessionButton = new JButton("Save Session");
        saveSessionButton.addActionListener(e -> {
            File sessionFile = FileUtils.showSaveSessionDialog(this);
            if (sessionFile != null) {
                FileUtils.saveSession(session, sessionFile);
            }
         }
        );
        mainPanel.add(saveSessionButton, gbc);
        saveSessionButton.setEnabled(false);

        startSessionButton = new JButton("Start Session");
        startSessionButton.addActionListener(e -> openSessionFrame());
        mainPanel.add(startSessionButton, gbc);
        startSessionButton.setEnabled(false);

        manageCardsButton = new JButton("Manage Cards");
        manageCardsButton.addActionListener(e -> openCardManagementFrame());
        manageCardsButton.setEnabled(false);
        mainPanel.add(manageCardsButton, gbc);

        showForgettingGraphButton = new JButton("Show Forgetting Graph");
        showForgettingGraphButton.addActionListener(e -> openForgettingGraphFrame());
        showForgettingGraphButton.setEnabled(false);
        mainPanel.add(showForgettingGraphButton, gbc);

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

        mainPanel.add(themeComboBox, gbc);

        add(mainPanel);
        pack();
        UITheme.setTheme(UITheme.getThemeCache().get("Arc"));
    }

    private void createSession() {
        session = FileUtils.createNewSession();
        manageCardsButton.setEnabled(true);
        startSessionButton.setEnabled(true);
        saveSessionButton.setEnabled(true);
        showForgettingGraphButton.setEnabled(true);
    }

    private void openForgettingGraphFrame() {
        ForgettingGraphFrame forgettingGraphFrame = new ForgettingGraphFrame(session);
        forgettingGraphFrame.setVisible(true);
    }

    private void loadSession() {
        File selectedFile = FileUtils.showLoadSessionDialog(this);
        if (selectedFile != null) {
            session = FileUtils.loadSession(selectedFile);
            manageCardsButton.setEnabled(true);
            startSessionButton.setEnabled(true);
            saveSessionButton.setEnabled(true);
            showForgettingGraphButton.setEnabled(true);
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