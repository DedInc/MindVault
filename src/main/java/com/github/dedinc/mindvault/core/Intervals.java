package com.github.dedinc.mindvault.core;

public class Intervals {
    public static final int[] intervals = {1, 3, 7, 14, 30, 60, 120, 240};

    public static boolean isReviseViolated(long learnDate, long[] reviseDates) {
        for (int i = 0; i < reviseDates.length; i++) {
            long daysSinceRevision = (reviseDates[i] - learnDate) / 86400;
            for (int interval : intervals) {
                if (daysSinceRevision > Math.round(interval + interval / 3.0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean needRevise(long learnDate, long reviseDate) {
        long currentDate = Time.getUnix();
        long daysSinceLearning = (currentDate - learnDate) / 86400;
        long daysSinceLastRevision = (currentDate - reviseDate) / 86400;
        long daysSinceLearningToRevision = daysSinceLearning - daysSinceLastRevision;
        int currentInterval = 0;
        for (int interval : intervals) {
            if (daysSinceLearningToRevision <= interval) {
                currentInterval = interval;
                break;
            }
        }
        return daysSinceLastRevision >= currentInterval;
    }
}