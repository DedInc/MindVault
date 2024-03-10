package com.github.dedinc.mindvault.ui;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.github.dedinc.mindvault.core.Grades;
import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.core.Time;
import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class GUI extends JFrame {
    private Session session;
    private JTextArea questionTextArea;
    private JTextField answerTextField;
    private JButton submitButton;
    private JButton addCardButton;
    private JButton removeCardButton;
    private JButton saveSessionButton;
    private JButton loadSessionButton;
    private JButton startSessionButton;
    private Queue<Card> cardQueue;
    private DefaultListModel<String> cardListModel;
    private JList<String> cardList;
    private JComboBox<String> themeComboBox;

    private StatusBar statusBar;

    public GUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("MindVault");
        setPreferredSize(new Dimension(850, 550));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        questionTextArea = UIComponents.createQuestionTextArea();
        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        mainPanel.add(questionScrollPane, BorderLayout.CENTER);

        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new FlowLayout());
        answerTextField = UIComponents.createAnswerTextField();
        submitButton = UIComponents.createSubmitButton();
        answerPanel.add(answerTextField);
        answerPanel.add(submitButton);
        mainPanel.add(answerPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        addCardButton = UIComponents.createAddCardButton();
        JButton addCardsFromFileButton = UIComponents.createAddCardsFromFileButton();
        removeCardButton = UIComponents.createRemoveCardButton();
        saveSessionButton = UIComponents.createSaveSessionButton();
        loadSessionButton = UIComponents.createLoadSessionButton();
        startSessionButton = UIComponents.createStartSessionButton();
        buttonPanel.add(addCardButton);
        buttonPanel.add(addCardsFromFileButton);
        buttonPanel.add(removeCardButton);
        buttonPanel.add(saveSessionButton);
        buttonPanel.add(loadSessionButton);
        buttonPanel.add(startSessionButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        cardListModel = new DefaultListModel<>();
        cardList = new JList<>(cardListModel);
        JScrollPane cardScrollPane = new JScrollPane(cardList);
        cardScrollPane.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(cardScrollPane, BorderLayout.EAST);

        add(mainPanel);
        pack();

        answerTextField.setEnabled(false);
        submitButton.setEnabled(false);

        UIEvents.addAnswerTextFieldKeyListener(answerTextField, this::submitAnswer);
        UIEvents.addSubmitButtonActionListener(submitButton, this::submitAnswer);
        UIEvents.addAddCardButtonActionListener(addCardButton, this::addCard);
        UIEvents.addAddCardsFromFileButtonActionListener(addCardsFromFileButton, this::addCardsFromFile);
        UIEvents.addRemoveCardButtonActionListener(removeCardButton, this::deleteSelectedCard);
        UIEvents.addSaveSessionButtonActionListener(saveSessionButton, this::saveSession);
        UIEvents.addLoadSessionButtonActionListener(loadSessionButton, this::loadSession);
        UIEvents.addStartSessionButtonActionListener(startSessionButton, this::startSession);

        themeComboBox = UITheme.createThemeComboBox();
        themeComboBox.addActionListener(e -> UITheme.changeTheme(this));
        answerPanel.add(themeComboBox);
        UITheme.setTheme(new FlatArcIJTheme());

        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadSession() {
        File selectedFile = FileUtils.showLoadSessionDialog(this);
        if (selectedFile != null) {
            session = FileUtils.loadSession(selectedFile);
            session.updateCards();
            updateCardList();
            statusBar.updateStatus(session);
            JOptionPane.showMessageDialog(this, "Session loaded successfully.");
        }
    }

    private void saveSession() {
        File selectedFile = FileUtils.showSaveSessionDialog(this);
        if (selectedFile != null) {
            FileUtils.saveSession(session, selectedFile);
            JOptionPane.showMessageDialog(this, "Session saved successfully.");
        }
    }

    private void addCard() {
        String question = JOptionPane.showInputDialog(this, "Enter the question:");
        String answer = JOptionPane.showInputDialog(this, "Enter the answer:");
        session.addCard(question, answer);
        updateCardList();
        JOptionPane.showMessageDialog(this, "Card added successfully.");
    }

    private void startSession() {
        if (session == null) {
            session = FileUtils.createNewSession();
        }
        cardQueue = new LinkedList<>(session.getCards());
        answerTextField.setEnabled(true);
        submitButton.setEnabled(true);
        displayNextCard();
    }

    private void submitAnswer() {
        String answer = answerTextField.getText();
        Card currentCard = cardQueue.peek();
        if (currentCard != null) {
            long startTime = Time.getUnix();
            double maxTime = Grades.calculateTypingTime(currentCard.getAnswer(), session.getTypeSpeed());
            double typeGrade = Grades.calculateTypeGrade(Time.getUnix() - startTime, maxTime);
            double accuracyGrade = Grades.calculateStringsRatio(currentCard.getAnswer(), answer);
            double totalGrade = Grades.calculateTotalGrade(new double[]{typeGrade, accuracyGrade});
            session.checkCard(currentCard, totalGrade);
            session.getGrades().add(totalGrade);

            if (session.getGrades().size() >= session.getPerSessionCards()) {
                session.updateLevel();
            }

            displayCorrectAnswer(currentCard, accuracyGrade);
            cardQueue.poll();
            updateCardList();
        }
        answerTextField.setText("");
        displayNextCard();
        statusBar.updateStatus(session);
    }

    private void displayCorrectAnswer(Card card, double accuracyGrade) {
        if (accuracyGrade >= 0.95) return;

        String correctAnswer = card.getAnswer();

        if (accuracyGrade <= 0.6) {
            String message = "Incorrect answer. The correct answer is:\n" + correctAnswer;
            JOptionPane.showMessageDialog(this, message, "Correct Answer", JOptionPane.ERROR_MESSAGE);
        } else {
            String message = "Nearly right answer. The correct answer is:\n" + correctAnswer;
            JOptionPane.showMessageDialog(this, message, "Correct Answer", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayNextCard() {
        Card nextCard = cardQueue.peek();
        if (nextCard != null) {
            questionTextArea.setText(nextCard.getQuestion());
        } else {
            questionTextArea.setText("No more cards available for this session.");
            answerTextField.setEnabled(false);
            submitButton.setEnabled(false);
        }
    }

    private void updateCardList() {
        cardListModel.clear();
        for (Card card : session.getAllCards()) {
            cardListModel.addElement(card.getQuestion());
        }
    }

    private void deleteSelectedCard() {
        String selectedCard = cardList.getSelectedValue();
        if (selectedCard != null) {
            session.removeCard(selectedCard);
            updateCardList();
            JOptionPane.showMessageDialog(this, "Card deleted successfully.");
        }
    }

    private void addCardsFromFile() {
        File selectedFile = FileUtils.showSelectFileDialog(this);
        if (selectedFile != null) {
            FileUtils.addCardsFromFile(selectedFile, session);
            updateCardList();
            JOptionPane.showMessageDialog(this, "Cards added successfully.");
        }
    }
}