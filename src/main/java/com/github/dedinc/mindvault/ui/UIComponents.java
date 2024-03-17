package com.github.dedinc.mindvault.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIComponents {

    public static JButton createButtonWithEvent(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
}