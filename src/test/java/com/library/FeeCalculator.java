package com.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorTest {

    @DisplayName("Boundary values: free period and the fee cap")
    @ParameterizedTest(name = "{0} days -> fee {1}")
    @CsvSource({
        "0,   0.0",   // minimum valid value
        "1,   0.0",
        "13,  0.0",   // just below the free-period limit
        "14,  0.0",   // on the limit: still free
        "15,  0.5",   // just above the limit: first paid day
        "16,  1.0",
        "20,  3.0",
        "33,  9.5",   // just below the cap
        "34,  10.0",  // exactly the cap
        "35,  10.0",  // above the cap: capped
        "1000, 10.0"
    })
    void calculatesFeeForValidDays(int days, double expected) {
        assertEquals(expected, FeeCalculator.calculateFee(days), 0.001);
    }

    @DisplayName("Negative testing: invalid number of days")
    @ParameterizedTest(name = "{0} days -> exception")
    @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
    void negativeDaysThrow(int days) {
        assertThrows(IllegalArgumentException.class, () -> FeeCalculator.calculateFee(days));
    }
}