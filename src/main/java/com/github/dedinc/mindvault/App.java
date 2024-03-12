package com.github.dedinc.mindvault;

import com.formdev.flatlaf.util.SystemInfo;
import com.github.dedinc.mindvault.ui.frames.MainMenuFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "MindVault");
            System.setProperty("apple.awt.application.appearance", "system");
        }

        if (SystemInfo.isLinux) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        java.awt.EventQueue.invokeLater(() -> {
            MainMenuFrame mainMenuFrame = new MainMenuFrame();
            mainMenuFrame.setVisible(true);
        });
    }
}