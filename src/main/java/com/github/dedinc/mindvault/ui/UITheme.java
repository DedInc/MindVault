package com.github.dedinc.mindvault.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.github.dedinc.mindvault.ui.render.ThemeComboBoxRenderer;

import javax.swing.*;

public class UITheme {
    private static JComboBox<String> themeComboBox;

    public static JComboBox<String> createThemeComboBox() {
        themeComboBox = new JComboBox<>();
        themeComboBox.addItem("Select Theme:");
        themeComboBox.addItem("================== Light Themes ==================");

        int i = 1;
        int lightIndex = i;
        int darkThemesIndex = lightIndex;
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo fijlafi : FlatAllIJThemes.INFOS) {
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

    public static void changeTheme(JFrame frame) {
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        if (selectedTheme != null && !selectedTheme.contains("Select Theme") && !selectedTheme.contains("Light Themes") && !selectedTheme.contains("Dark Themes")) {
            for (FlatAllIJThemes.FlatIJLookAndFeelInfo fijlafi : FlatAllIJThemes.INFOS) {
                if (fijlafi.getName().equals(selectedTheme)) {
                    try {
                        Class<?> themeClass = Class.forName(fijlafi.getClassName());
                        IntelliJTheme.ThemeLaf themeInstance = (IntelliJTheme.ThemeLaf) themeClass.getDeclaredConstructor().newInstance();
                        setTheme(themeInstance);
                        SwingUtilities.updateComponentTreeUI(frame);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void setTheme(IntelliJTheme.ThemeLaf themeInstance) {
        try {
            UIManager.setLookAndFeel(themeInstance);
            FlatLaf.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}