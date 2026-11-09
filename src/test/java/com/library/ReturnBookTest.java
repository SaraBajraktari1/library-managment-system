package com.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReturnBookTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book("111", "Clean Code", "Robert C. Martin"));
    }

    @Test
    void returnBorrowedBookMakesItAvailable() {
        library.borrowBook("111", "M001");
        library.returnBook("111");
        assertFalse(library.findByIsbn("111").isBorrowed());
        assertNull(library.findByIsbn("111").getBorrowedBy());
    }

    @Test
    void returnBookThatIsNotBorrowedThrows() {
        assertThrows(IllegalStateException.class, () -> library.returnBook("111"));
    }

    @Test
    void returnUnknownBookThrows() {
        assertThrows(IllegalArgumentException.class, () -> library.returnBook("999"));
    }
}