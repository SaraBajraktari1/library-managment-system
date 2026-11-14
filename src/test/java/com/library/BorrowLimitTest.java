package com.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class BorrowLimitTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        for (int i = 1; i <= 5; i++) {
            library.addBook(new Book("B" + i, "Book " + i, "Author"));
        }
    }

    private void borrowBooks(String memberId, int count) {
        for (int i = 1; i <= count; i++) {
            library.borrowBook("B" + i, memberId);
        }
    }

    @ParameterizedTest(name = "member can borrow {0} book(s)")
    @ValueSource(ints = {1, 2, 3})
    void memberCanBorrowUpToTheLimit(int count) {
        borrowBooks("M001", count);
        assertEquals(count, library.getBorrowedCount("M001"));
    }

    @Test
    void borrowingBeyondTheLimitThrows() {
        borrowBooks("M001", Library.MAX_LOANS);
        assertThrows(IllegalStateException.class, () -> library.borrowBook("B4", "M001"));
        assertFalse(library.findByIsbn("B4").isBorrowed());
    }

    @Test
    void limitAppliesPerMember() {
        borrowBooks("M001", Library.MAX_LOANS);
        library.borrowBook("B4", "M002");
        assertEquals(1, library.getBorrowedCount("M002"));
    }

    @Test
    void canBorrowAgainAfterReturningABook() {
        borrowBooks("M001", Library.MAX_LOANS);
        library.returnBook("B1");
        library.borrowBook("B4", "M001");
        assertEquals(Library.MAX_LOANS, library.getBorrowedCount("M001"));
    }

    @Test
    void newMemberHasZeroLoans() {
        assertEquals(0, library.getBorrowedCount("M999"));
    }
}