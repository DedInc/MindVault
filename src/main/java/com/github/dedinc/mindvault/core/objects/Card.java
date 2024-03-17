package com.github.dedinc.mindvault.core.objects;

import com.github.dedinc.mindvault.core.Session;

import java.util.List;
import java.util.Map;

public class Card {
    private String question;
    private String answer;
    private long learnDate;
    private long[] reviseDates;
    private State cachedCategory;

    public Card(String question, String answer, long learnDate, long[] reviseDates) {
        this.question = question;
        this.answer = answer;
        this.learnDate = learnDate;
        this.reviseDates = reviseDates;
        this.cachedCategory = null;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public long getLearnDate() {
        return learnDate;
    }

    public void setLearnDate(long learnDate) {
        this.learnDate = learnDate;
        this.cachedCategory = null;
    }

    public long[] getReviseDates() {
        return reviseDates;
    }

    public void setReviseDates(long[] reviseDates) {
        this.reviseDates = reviseDates;
        this.cachedCategory = null;
    }

    public State getCategory(Session session) {
        if (cachedCategory == null) {
            Map<State, List<Card>> categories = session.getCategories();
            for (State state : categories.keySet()) {
                if (categories.get(state).contains(this)) {
                    cachedCategory = state;
                    break;
                }
            }
        }
        return cachedCategory;
    }
}