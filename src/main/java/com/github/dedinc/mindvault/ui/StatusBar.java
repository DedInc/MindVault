package com.github.dedinc.mindvault.ui;

import com.github.dedinc.mindvault.core.Grades;
import com.github.dedinc.mindvault.core.Session;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private JLabel cardsLearnedLabel;
    private JLabel totalGradeLabel;


    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        cardsLearnedLabel = new JLabel();
        totalGradeLabel = new JLabel();
        add(cardsLearnedLabel);
        add(totalGradeLabel);
    }

    public void updateStatus(Session session) {
        int cardsLearned = session.getPerSessionCards();
        double totalGrade = Grades.calculateTotalGrade(session.getGrades());

        cardsLearnedLabel.setText("Learn per session: " + cardsLearned);

        totalGradeLabel.setText("Total Grade: " + String.format("%.2f", totalGrade));

        Color gradeColor;
        if (totalGrade >= 0.8) {
            gradeColor = Color.GREEN;
        } else if (totalGrade >= 0.6) {
            gradeColor = Color.ORANGE;
        } else {
            gradeColor = Color.RED;
        }
        totalGradeLabel.setForeground(gradeColor);
    }
}