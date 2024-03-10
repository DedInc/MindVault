package com.github.dedinc.mindvault.ui;

import javax.swing.*;

public class UIComponents {
    public static JTextArea createQuestionTextArea() {
        JTextArea questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        return questionTextArea;
    }

    public static JTextField createAnswerTextField() {
        return new JTextField(20);
    }

    public static JButton createSubmitButton() {
        return new JButton("Submit");
    }

    public static JButton createAddCardButton() {
        return new JButton("Add Card");
    }

    public static JButton createAddCardsFromFileButton() {
        return new JButton("Add Cards from File");
    }

    public static JButton createRemoveCardButton() {
        return new JButton("Remove Card");
    }

    public static JButton createSaveSessionButton() {
        return new JButton("Save Session");
    }

    public static JButton createLoadSessionButton() {
        return new JButton("Load Session");
    }

    public static JButton createStartSessionButton() {
        return new JButton("Start/Create Session");
    }
}