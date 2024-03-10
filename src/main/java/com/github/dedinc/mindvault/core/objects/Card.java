package com.github.dedinc.mindvault.core.objects;

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
}
