package com.github.dedinc.mindvault.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class Grades {
    public static int calculateTypeSpeed(long startTime, String text) {
        long endTime = Time.getUnix();
        long typeTime = endTime - startTime;
        return Math.round(text.length() / typeTime);
    }

    public static double calculateTypingTime(String text, int charsPerSecond, Session session) {
        double typingTime = text.length() / (double) charsPerSecond;
        double thinkingTime = typingTime * 0.3 + calculateTotalGrade(session.getGrades()) >= 0.85 ? 3 : 5;
        return round(typingTime + thinkingTime);
    }

    public static double calculateTypeGrade(double typedTime, double maxTime) {
        return round(Math.max(Math.min(maxTime / typedTime, 1), 0));
    }

    public static double calculateStringsRatio(String s1, String s2) {
        String longer = s1.length() >= s2.length() ? s1 : s2;
        String shorter = s1.length() < s2.length() ? s1 : s2;
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0;
        long matchedCount = shorter.chars().filter(c -> longer.indexOf(c) != -1).count();
        return round((double) matchedCount / longerLength);
    }

    public static double calculateTotalGrade(List<Double> grades) {
        if (grades.isEmpty()) return 0.0;
        Collections.sort(grades);
        int midIndex = grades.size() / 2;
        double median;
        if (grades.size() % 2 == 0) {
            median = (grades.get(midIndex - 1) + grades.get(midIndex)) / 2.0;
        } else {
            median = grades.get(midIndex);
        }
        return Math.min(round(median), 1.0);
    }

    private static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}