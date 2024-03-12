package com.github.dedinc.mindvault.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.github.dedinc.mindvault.ui.render.ThemeComboBoxRenderer;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class UITheme {
    private static JComboBox<String> themeComboBox;
    private static Map<String, String> themeCache = new HashMap<>();

    public static JComboBox<String> createThemeComboBox() {
        themeComboBox = new JComboBox<>() {
            @Override
            public void processKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.consume();
                } else {
                    super.processKeyEvent(e);
                }
            }
        };
        themeComboBox.addItem("Select Theme:");
        themeComboBox.addItem("================== Light Themes ==================");
        int i = 1;
        int lightIndex = i;
        int darkThemesIndex = lightIndex;
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo fijlafi : FlatAllIJThemes.INFOS) {
            themeCache.put(fijlafi.getName(), fijlafi.getClassName());
            if (!fijlafi.isDark()) {
                themeComboBox.addItem(fijlafi.getName());
                darkThemesIndex++;
            }
        }
        themeComboBox.addItem("================== Dark Themes ==================");
        darkThemesIndex++;
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo fijlafi : FlatAllIJThemes.INFOS) {
            if (fijlafi.isDark()) {
                themeComboBox.addItem(fijlafi.getName());
            }
        }
        themeComboBox.setRenderer(new ThemeComboBoxRenderer(1, darkThemesIndex));
        return themeComboBox;
    }

    public static void changeTheme() {
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        if (selectedTheme != null && !selectedTheme.contains("Select Theme") && !selectedTheme.contains("Light Themes") && !selectedTheme.contains("Dark Themes")) {
            for (FlatAllIJThemes.FlatIJLookAndFeelInfo fijlafi : FlatAllIJThemes.INFOS) {
                if (fijlafi.getName().equals(selectedTheme)) {
                    setTheme(themeCache.get(fijlafi.getName()));
                }
            }
        }
    }

    public static void setTheme(String className) {
        try {
            UIManager.setLookAndFeel(className);
            FlatLaf.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getThemeCache() {
        return themeCache;
    }
}