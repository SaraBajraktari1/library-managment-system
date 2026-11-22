package com.library;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TransactionRequest(
        @NotBlank(message = "bookIsbn is required") String bookIsbn,
        @NotBlank(message = "memberId is required") String memberId,
        @Min(value = 0, message = "daysBorrowed cannot be negative") int daysBorrowed) {
}