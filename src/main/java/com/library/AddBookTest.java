package com.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddBookTest {
    private Library library;

    @BeforeEach
    void setUp() { library = new Library(); }

    @Test
    void addValidBookIncreasesCount() {
        library.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch"));
        assertEquals(1, library.getBookCount());
        assertEquals("Effective Java", library.findByIsbn("978-0134685991").getTitle());
    }

    @Test
    void addNullBookThrows() {
        assertThrows(IllegalArgumentException.class, () -> library.addBook(null));
    }

    @Test
    void addBookWithEmptyIsbnThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> library.addBook(new Book("", "Title", "Author")));
    }

    @Test
    void addBookWithBlankTitleThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> library.addBook(new Book("111", "   ", "Author")));
    }

    @Test
    void addDuplicateIsbnThrows() {
        library.addBook(new Book("111", "Book A", "Author"));
        assertThrows(IllegalStateException.class,
            () -> library.addBook(new Book("111", "Book B", "Author")));
        assertEquals(1, library.getBookCount());
    }
}