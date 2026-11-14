package com.library;

public class FeeCalculator {
    public static final int FREE_DAYS = 14;

    public static double calculateFee(int daysBorrowed) {
        if (daysBorrowed < 0) throw new IllegalArgumentException("Days cannot be negative");
        if (daysBorrowed <= FREE_DAYS) return 0.0;
        return Math.min((daysBorrowed - FREE_DAYS) * 0.5, 10.0);
    }
}