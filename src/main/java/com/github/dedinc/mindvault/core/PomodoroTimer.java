package com.github.dedinc.mindvault.core;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PomodoroTimer {
    private static final List<Integer> POMODORO_INTERVALS = Arrays.asList(20, 5, 20, 5, 20, 15);
    public static final int MAX_BLOCKS_PER_DAY = 3;

    private int currentInterval;
    private int completedBlocks;
    private LocalDate lastTrainingDate;

    public PomodoroTimer() {
        currentInterval = 0;
        completedBlocks = 0;
        lastTrainingDate = LocalDate.now();
    }

    public int getCurrentInterval() {
        return POMODORO_INTERVALS.get(currentInterval);
    }

    public boolean isBreakInterval() {
        return currentInterval % 2 != 0;
    }

    public void nextInterval() {
        currentInterval = (currentInterval + 1) % POMODORO_INTERVALS.size();
        if (currentInterval == 0) {
            completedBlocks++;
        }
    }

    public int getCompletedBlocks() {
        return completedBlocks;
    }

    public LocalDate getLastTrainingDate() {
        return lastTrainingDate;
    }

    public void setCompletedBlocks(int completedBlocks) {
        this.completedBlocks = completedBlocks;
    }

    public void setLastTrainingDate(LocalDate lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }

    public boolean canStartSession() {
        return !isBreakInterval() && completedBlocks < MAX_BLOCKS_PER_DAY;
    }
}