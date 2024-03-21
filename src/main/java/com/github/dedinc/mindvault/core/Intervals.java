package com.github.dedinc.mindvault.core;

public class Intervals {
    public static final int[] intervals = {1, 3, 7, 14, 30, 60, 120, 240};

    public static boolean isReviseViolated(long learnDate, long[] reviseDates) {
        for (int i = 0; i < reviseDates.length; i++) {
            long daysSinceRevision = (reviseDates[i] - learnDate) / 86400;
            for (int interval : intervals) {
                if (daysSinceRevision > Math.round(interval + interval / 3.5)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean needRevise(long learnDate, long[] reviseDates) {
        long currentDate = Time.getUnix();
        long daysSinceLearning = (currentDate - learnDate) / 86400;

        int currentInterval = 0;
        int revisionCount = reviseDates.length;

        if (revisionCount > 0) {
            long lastRevisionDate = reviseDates[revisionCount - 1];
            long daysSinceLastRevision = (currentDate - lastRevisionDate) / 86400;

            if (revisionCount < intervals.length) {
                currentInterval = intervals[revisionCount];
            } else {
                currentInterval = intervals[intervals.length - 1];
            }

            return daysSinceLastRevision >= currentInterval;
        } else {
            currentInterval = intervals[0];
            return daysSinceLearning >= currentInterval;
        }
    }
}