package com.github.dedinc.mindvault;

import com.github.dedinc.mindvault.ui.GUI;

public class App {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }
}