package com.github.dedinc.mindvault.core;

import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.core.objects.State;

import java.util.*;

public class Session {
    private int typeSpeed;
    private Map<State, List<Card>> cards;
    private List<Double> grades;
    private int perSessionCards;

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
        this.pomodoroTimer = new PomodoroTimer();
    }

    public void addCard(String question, String answer) {
        Card newCard = new Card(question, answer, 0, new long[0]);
        cards.get(State.LEARN).add(newCard);
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
        for (State category : Arrays.asList(State.REVISE, State.WEAK, State.MIDDLE, State.LEARN)) {
            cards.get(category).removeIf(c -> c.getQuestion().equals(card.getQuestion()));
        }
        cards.get(newState).add(card);
    }

    public void updateCards() {
        for (State category : cards.keySet()) {
            List<Card> categoryCards = cards.get(category);
            List<Card> tempCards = new ArrayList<>();
            for (Card tempCard : categoryCards) {
                tempCards.add(tempCard);
            }
            for (Card card : tempCards) {
                if (category == State.LEARN || category == State.REVISE) {
                    continue;
                }
                boolean reviseViolated = Intervals.isReviseViolated(card.getLearnDate(), card.getReviseDates());
                boolean needRevise = Intervals.needRevise(card.getLearnDate(), card.getReviseDates());
                if (reviseViolated) {
                    card.setLearnDate(0);
                    card.setReviseDates(new long[0]);
                    moveCard(card, State.LEARN);
                } else if (needRevise) {
                    moveCard(card, State.REVISE);
                }
            }
        }
    }

    public void checkCard(Card card, double grade) {
        State currentState = card.getCategory(this);

        if (currentState == State.LEARN) {
            card.setLearnDate(Time.getUnix());
            moveCard(card, State.WEAK);
            return;
        }
        if (currentState == State.REVISE) {
            int revises = card.getReviseDates().length;
            long[] newReviseDates = Arrays.copyOf(card.getReviseDates(), revises + 1);
            newReviseDates[revises] = Time.getUnix();
            card.setReviseDates(newReviseDates);
            if (revises == 0) {
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
                System.out.println("Move...");
                moveCard(card, State.STRONG);
            } else if (grade < 0.6) {
                moveCard(card, State.WEAK);
            }
        }
    }

    public void updateLevel() {
        double grade = Grades.calculateTotalGrade(grades);
        perSessionCards = Math.min(
                grade >= 0.9 ? perSessionCards + 3 : grade >= 0.75 ? perSessionCards + 2 : grade <= 0.6 ? perSessionCards - 1 : perSessionCards,
                10
        );
        perSessionCards = Math.max(perSessionCards, 5);
        grades.clear();
    }

    public List<Card> getCards() {
        List<Card> selectedCards = new ArrayList<>();
        boolean hasCardsToReview = !cards.get(State.REVISE).isEmpty() || !cards.get(State.WEAK).isEmpty() || !cards.get(State.MIDDLE).isEmpty();

        if (hasCardsToReview) {
            for (State category : Arrays.asList(State.REVISE, State.WEAK, State.MIDDLE)) {
                List<Card> sourceCards = new ArrayList<>(cards.get(category));
                int limit = Math.min(perSessionCards - selectedCards.size(), sourceCards.size());
                if (selectedCards.size() < perSessionCards && !sourceCards.isEmpty()) {
                    selectedCards.addAll(sourceCards.subList(0, limit));
                }
            }
        } else {
            List<Card> sourceCards = new ArrayList<>(cards.get(State.LEARN));
            int learnedToday = (int) sourceCards.stream()
                    .filter(card -> Time.isToday(card.getLearnDate()))
                    .count();

            if (learnedToday < 100) {
                int limit = Math.min(perSessionCards - selectedCards.size(), sourceCards.size());
                int remainingQuota = 100 - learnedToday;
                int newCardsToLearn = Math.min(limit, remainingQuota);

                if (selectedCards.size() < perSessionCards && !sourceCards.isEmpty()) {
                    selectedCards.addAll(sourceCards.subList(0, newCardsToLearn));
                }
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
        List<Card> allCards = new ArrayList<>();
        for (List<Card> cards : getCategories().values()) {
            for (Card card : cards) {
                allCards.add(card);
            }
        }
        return allCards;
    }

    public void setTypeSpeed(int typeSpeed) {
        this.typeSpeed = typeSpeed;
    }

    public void setCards(Map<State, List<Card>> cards) {
        this.cards = cards;
    }

    public void setGrades(List<Double> grades) {
        this.grades = grades;
    }

    public void setPerSessionCards(int perSessionCards) {
        this.perSessionCards = perSessionCards;
    }

    public PomodoroTimer getPomodoroTimer() {
        return pomodoroTimer;
    }
}