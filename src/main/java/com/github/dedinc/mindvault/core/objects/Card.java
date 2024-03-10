package com.github.dedinc.mindvault.core.objects;

import com.github.dedinc.mindvault.core.Session;

import java.util.List;
import java.util.Map;

public class Card {
    private String question;
    private String answer;
    private long learnDate;
    private long[] reviseDates;

    public Card(String question, String answer, long learnDate, long[] reviseDates) {
        this.question = question;
        this.answer = answer;
        this.learnDate = learnDate;
        this.reviseDates = reviseDates;
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
    }

    public long[] getReviseDates() {
        return reviseDates;
    }

    public void setReviseDates(long[] reviseDates) {
        this.reviseDates = reviseDates;
    }

    public State getCategory(Session session) {
        Map<State, List<Card>> categories = session.getCategories();
        for (State state : categories.keySet()) {
            if (categories.get(state).contains(this)) {
                return state;
            }
        }
        return null;
    }
}
