package com.github.dedinc.mindvault.core;

import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.core.objects.State;

import java.util.*;
import java.util.stream.Collectors;

public class Session {
    private int typeSpeed;
    private Map<State, List<Card>> cards;
    private List<Double> grades;
    private int perSessionCards;
    private List<Card> allCardsCache;

    private PomodoroTimer pomodoroTimer;

    public Session() {
        this(Time.getUnix() - 1, "\n");
    }

    public Session(long startTime, String text) {
        this.typeSpeed = Grades.calculateTypeSpeed(startTime, text);
        this.cards = new EnumMap<>(State.class);
        for (State state : State.values()) {
            this.cards.put(state, new ArrayList<>());
        }
        this.grades = new ArrayList<>();
        this.perSessionCards = 5;
        this.allCardsCache = null;
        this.pomodoroTimer = new PomodoroTimer();
    }

    public void addCard(String question, String answer) {
        Card newCard = new Card(question, answer, 0, new long[0]);
        cards.get(State.LEARN).add(newCard);
        invalidateAllCardsCache();
    }

    public void addCards(List<Card> cards) {
        for (Card card : cards) {
            addCard(card.getQuestion(), card.getAnswer());
        }
    }

    public void removeCard(String question) {
        for (List<Card> cardList : cards.values()) {
            cardList.removeIf(card -> card.getQuestion().equals(question));
        }
        invalidateAllCardsCache();
    }

    public void removeCards(List<String> questions) {
        for (String question : questions) {
            removeCard(question);
        }
    }

    public void moveCard(Card card, State newState) {
        State currentState = card.getCategory(this);
        if (currentState == newState) {
            return;
        }
        cards.get(currentState).removeIf(c -> c.getQuestion().equals(card.getQuestion()));
        cards.get(newState).add(card);
        invalidateAllCardsCache();
    }

    public void updateCards() {
        Map<State, List<Card>> cardsToMove = new EnumMap<>(State.class);
        for (List<Card> cardList : cards.values()) {
            List<Card> learnCards = new ArrayList<>();
            List<Card> reviseCards = new ArrayList<>();
            for (Card card : cardList) {
                State category = card.getCategory(this);
                if (category == State.LEARN || category == State.REVISE) {
                    continue;
                }
                boolean reviseViolated = Intervals.isReviseViolated(card.getLearnDate(), card.getReviseDates());
                boolean needRevise = Intervals.needRevise(card.getLearnDate(), Arrays.stream(card.getReviseDates()).max().orElse(0));
                if (reviseViolated) {
                    card.setLearnDate(0);
                    card.setReviseDates(new long[0]);
                    learnCards.add(card);
                } else if (needRevise) {
                    reviseCards.add(card);
                }
            }
            cardList.removeAll(learnCards);
            cardList.removeAll(reviseCards);
            cardsToMove.computeIfAbsent(State.LEARN, k -> new ArrayList<>()).addAll(learnCards);
            cardsToMove.computeIfAbsent(State.REVISE, k -> new ArrayList<>()).addAll(reviseCards);
        }
        for (Map.Entry<State, List<Card>> entry : cardsToMove.entrySet()) {
            cards.get(entry.getKey()).addAll(entry.getValue());
        }
        invalidateAllCardsCache();
    }

    public void checkCard(Card card, double grade) {
        State currentState = card.getCategory(this);
        if (currentState == State.LEARN) {
            card.setLearnDate(Time.getUnix());
            moveCard(card, State.WEAK);
            return;
        }
        if (currentState == State.REVISE) {
            List<Long> reviseDates = new ArrayList<>(Arrays.asList(Arrays.stream(card.getReviseDates()).boxed().toArray(Long[]::new)));
            reviseDates.add(Time.getUnix());
            card.setReviseDates(reviseDates.stream().mapToLong(Long::longValue).toArray());
            if (reviseDates.size() == 1) {
                moveCard(card, State.WEAK);
            } else {
                moveCard(card, grade >= 0.80 ? State.STRONG : grade <= 0.6 ? State.WEAK : State.MIDDLE);
            }
            return;
        }
        if (currentState == State.WEAK && grade >= 0.6) {
            moveCard(card, State.MIDDLE);
            return;
        }
        if (currentState == State.MIDDLE) {
            if (grade >= 0.85) {
                moveCard(card, State.STRONG);
            } else if (grade < 0.6) {
                moveCard(card, State.WEAK);
            }
            return;
        }
        if (currentState == State.STRONG && grade <= 0.8) {
            moveCard(card, State.MIDDLE);
        }
    }

    public void updateLevel() {
        if (!grades.isEmpty()) {
            double grade = Grades.calculateTotalGrade(grades);
            perSessionCards = Math.min(
                    grade >= 0.9 ? perSessionCards + 3 : grade >= 0.75 ? perSessionCards + 2 : grade <= 0.6 ? perSessionCards - 1 : perSessionCards,
                    10
            );
            perSessionCards = Math.max(perSessionCards, 5);
            grades.clear();
        }
    }

    public List<Card> getCards() {
        List<Card> selectedCards = new ArrayList<>();
        for (State category : Arrays.asList(State.REVISE, State.WEAK, State.MIDDLE, State.LEARN)) {
            List<Card> sourceCards = new ArrayList<>(cards.get(category));
            int limit = Math.min(perSessionCards - selectedCards.size(), sourceCards.size());
            if (selectedCards.size() < perSessionCards && !sourceCards.isEmpty()) {
                selectedCards.addAll(sourceCards.subList(0, limit));
            }
        }
        return selectedCards;
    }

    public List<Double> getGrades() {
        return grades;
    }

    public int getTypeSpeed() {
        return typeSpeed;
    }

    public int getPerSessionCards() {
        return perSessionCards;
    }

    public Map<State, List<Card>> getCategories() {
        return cards;
    }

    public List<Card> getAllCards() {
        if (allCardsCache == null) {
            allCardsCache = cards.values().stream().flatMap(List::stream).collect(Collectors.toList());
        }
        return allCardsCache;
    }

    public void setTypeSpeed(int typeSpeed) {
        this.typeSpeed = typeSpeed;
    }

    public void setCards(Map<State, List<Card>> cards) {
        this.cards = cards;
        invalidateAllCardsCache();
    }

    public void setGrades(List<Double> grades) {
        this.grades = grades;
    }

    public void setPerSessionCards(int perSessionCards) {
        this.perSessionCards = perSessionCards;
    }

    private void invalidateAllCardsCache() {
        allCardsCache = null;
    }

    public PomodoroTimer getPomodoroTimer() {
        return pomodoroTimer;
    }
}