package com.github.dedinc.mindvault.core;

import java.util.Arrays;
import java.util.Locale;

public class Grades {
    public static int calculateTypeSpeed(long startTime, String text) {
        long endTime = Time.getUnix();
        long typeTime = endTime - startTime;
        return Math.round(text.length() / typeTime);
    }

    public static double calculateTypingTime(String text, int charsPerSecond) {
        double typingTime = text.length() / (double) charsPerSecond;
        double thinkingTime = typingTime * 0.3;
        return Double.parseDouble(String.format(Locale.US, "%.2f", typingTime + thinkingTime));
    }

    public static double calculateTypeGrade(double typedTime, double maxTime) {
        return Double.parseDouble(String.format(Locale.US, "%.2f", Math.max(Math.min(maxTime / typedTime, 1), 0)));
    }

    public static double calculateStringsRatio(String s1, String s2) {
        String longer = s1.length() >= s2.length() ? s1 : s2;
        String shorter = s1.length() < s2.length() ? s1 : s2;
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0;
        int matchedCount = (int) shorter.chars()
                .filter(c -> c == longer.charAt(shorter.indexOf(c)))
                .count();
        return Double.parseDouble(String.format(Locale.US, "%.2f", (double) matchedCount / longerLength));
    }

    public static double calculateTotalGrade(double[] grades) {
        Arrays.sort(grades);
        int midIndex = grades.length / 2;
        double median;
        if (grades.length > 0) {
            if (grades.length % 2 == 0) {
                median = (grades[midIndex - 1] + grades[midIndex]) / 2.0;
            } else {
                median = grades[midIndex];
            }
            String formattedMedian = String.format(Locale.US, "%.2f", median);
            return Math.min(Double.parseDouble(formattedMedian), 1.0);
        }
        return 0.0;
    }
}