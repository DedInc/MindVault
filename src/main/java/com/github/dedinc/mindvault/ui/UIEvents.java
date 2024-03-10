package com.github.dedinc.mindvault.ui;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UIEvents {
    public static void addAnswerTextFieldKeyListener(JTextField answerTextField, Runnable submitAnswer) {
        answerTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitAnswer.run();
                }
            }
        });
    }

    public static void addSubmitButtonActionListener(JButton submitButton, Runnable submitAnswer) {
        submitButton.addActionListener(e -> submitAnswer.run());
    }

    public static void addAddCardButtonActionListener(JButton addCardButton, Runnable addCard) {
        addCardButton.addActionListener(e -> addCard.run());
    }

    public static void addAddCardsFromFileButtonActionListener(JButton addCardsFromFileButton, Runnable addCardsFromFile) {
        addCardsFromFileButton.addActionListener(e -> addCardsFromFile.run());
    }

    public static void addRemoveCardButtonActionListener(JButton removeCardButton, Runnable deleteSelectedCard) {
        removeCardButton.addActionListener(e -> deleteSelectedCard.run());
    }

    public static void addSaveSessionButtonActionListener(JButton saveSessionButton, Runnable saveSession) {
        saveSessionButton.addActionListener(e -> saveSession.run());
    }

    public static void addLoadSessionButtonActionListener(JButton loadSessionButton, Runnable loadSession) {
        loadSessionButton.addActionListener(e -> loadSession.run());
    }

    public static void addStartSessionButtonActionListener(JButton startSessionButton, Runnable startSession) {
        startSessionButton.addActionListener(e -> startSession.run());
    }
}