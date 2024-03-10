package com.github.dedinc.mindvault.ui.render;

import javax.swing.*;
import java.awt.*;

public class ThemeComboBoxRenderer extends DefaultListCellRenderer {
    private final int lightThemesIndex;
    private final int darkThemesIndex;

    public ThemeComboBoxRenderer(int lightThemesIndex, int darkThemesIndex) {
        this.lightThemesIndex = lightThemesIndex;
        this.darkThemesIndex = darkThemesIndex;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (index == lightThemesIndex || index == darkThemesIndex) {
            setText(getText());
            setEnabled(false);
        } else {
            setEnabled(true);
        }

        return this;
    }
}