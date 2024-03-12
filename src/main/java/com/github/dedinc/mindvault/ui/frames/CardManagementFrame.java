package com.github.dedinc.mindvault.ui.frames;

import com.github.dedinc.mindvault.core.Session;
import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.ui.UIComponents;
import com.github.dedinc.mindvault.ui.UIEvents;
import com.github.dedinc.mindvault.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CardManagementFrame extends JFrame {
    private Session session;
    private JButton addCardButton;
    private JButton removeCardButton;
    private JButton addCardsFromFileButton;
    private DefaultListModel<String> cardListModel;
    private JList<String> cardList;

    public CardManagementFrame(Session session) {
        this.session = session;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("MindVault - Card Management");
        setPreferredSize(new Dimension(400, 300));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        addCardButton = UIComponents.createAddCardButton();
        addCardsFromFileButton = UIComponents.createAddCardsFromFileButton();
        removeCardButton = UIComponents.createRemoveCardButton();
        buttonPanel.add(addCardButton);
        buttonPanel.add(addCardsFromFileButton);
        buttonPanel.add(removeCardButton);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        cardListModel = new DefaultListModel<>();
        cardList = new JList<>(cardListModel);
        JScrollPane cardScrollPane = new JScrollPane(cardList);
        mainPanel.add(cardScrollPane, BorderLayout.CENTER);

        add(mainPanel);
        pack();

        UIEvents.addAddCardButtonActionListener(addCardButton, this::addCard);
        UIEvents.addAddCardsFromFileButtonActionListener(addCardsFromFileButton, this::addCardsFromFile);
        UIEvents.addRemoveCardButtonActionListener(removeCardButton, this::deleteSelectedCard);

        updateCardList();
    }

    private void addCard() {
        String question = JOptionPane.showInputDialog(this, "Enter the question:");
        String answer = JOptionPane.showInputDialog(this, "Enter the answer:");
        session.addCard(question, answer);
        updateCardList();
        JOptionPane.showMessageDialog(this, "Card added successfully.");
    }

    private void addCardsFromFile() {
        File selectedFile = FileUtils.showSelectFileDialog(this);
        if (selectedFile != null) {
            FileUtils.addCardsFromFile(selectedFile, session);
            updateCardList();
            JOptionPane.showMessageDialog(this, "Cards added successfully.");
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

    private void updateCardList() {
        cardListModel.clear();
        for (Card card : session.getAllCards()) {
            cardListModel.addElement(card.getQuestion());
        }
    }
}