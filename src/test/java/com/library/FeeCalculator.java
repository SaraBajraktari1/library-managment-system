package com.library;

public class FeeCalculator {
    public static final int FREE_DAYS = 14;
    public static final double DAILY_FEE = 0.5;
    public static final double MAX_FEE = 10.0;

    public static double calculateFee(int daysBorrowed) {
        if (daysBorrowed < 0) throw new IllegalArgumentException("Days cannot be negative");
        int lateDays = Math.max(0, daysBorrowed - FREE_DAYS);
        return Math.min(lateDays * DAILY_FEE, MAX_FEE);
    }
}