package com.github.dedinc.mindvault.ui.frames;

import com.github.dedinc.mindvault.core.Grades;
import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.core.Time;
import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.ui.StatusBar;
import com.github.dedinc.mindvault.ui.UIComponents;
import com.github.dedinc.mindvault.ui.UIEvents;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class SessionFrame extends JFrame {
    private Session session;
    private JTextArea questionTextArea;
    private JTextField answerTextField;
    private JButton submitButton;
    private Queue<Card> cardQueue;
    private StatusBar statusBar;

    public SessionFrame(Session session) {
        this.session = session;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("MindVault - Session");
        setPreferredSize(new Dimension(600, 400));

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

        add(mainPanel);
        pack();

        answerTextField.setEnabled(false);
        submitButton.setEnabled(false);

        UIEvents.addAnswerTextFieldKeyListener(answerTextField, this::submitAnswer);
        UIEvents.addSubmitButtonActionListener(submitButton, this::submitAnswer);

        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private void startSession() {
        cardQueue = new LinkedList<>(session.getCards());
        answerTextField.setEnabled(true);
        submitButton.setEnabled(true);
        displayNextCard();
    }

    @Override
    public void setVisible(boolean visible) {
        if (session.getCards().isEmpty() && visible) {
            JOptionPane.showMessageDialog(null, "No cards to learn today!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.setVisible(visible);
        if (visible) {
            startSession();
        }
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
            setVisible(false);
        }
    }
}