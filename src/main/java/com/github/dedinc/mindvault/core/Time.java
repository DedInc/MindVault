package com.github.dedinc.mindvault.core;

public class Time {
    public static long getUnix() {
        return System.currentTimeMillis() / 1000;
    }
}