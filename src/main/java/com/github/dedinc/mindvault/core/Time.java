package com.github.dedinc.mindvault.core;

import java.time.LocalDate;

public class Time {
    public static long getUnix() {
        return System.currentTimeMillis() / 1000;
    }

    public static boolean isToday(long timestamp) {
        LocalDate date = LocalDate.ofEpochDay(timestamp / 86400);
        LocalDate today = LocalDate.now();
        return date.equals(today);
    }
}