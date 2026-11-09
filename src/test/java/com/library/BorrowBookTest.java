package com.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BorrowBookTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book("111", "Clean Code", "Robert C. Martin"));
    }

    @Test
    void borrowAvailableBookMarksItBorrowed() {
        library.borrowBook("111", "M001");
        assertTrue(library.findByIsbn("111").isBorrowed());
        assertEquals("M001", library.findByIsbn("111").getBorrowedBy());
    }

    @Test
    void borrowUnknownBookThrows() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("999", "M001"));
    }

    @Test
    void borrowAlreadyBorrowedBookThrows() {
        library.borrowBook("111", "M001");
        assertThrows(IllegalStateException.class, () -> library.borrowBook("111", "M002"));
    }

    @Test
    void borrowWithEmptyMemberIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("111", ""));
    }
}