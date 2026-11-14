package com.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addBook(new Book("111", "Clean Code", "Robert C. Martin"));
    }

    @ParameterizedTest(name = "invalid ISBN [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void addBookWithInvalidIsbnThrows(String isbn) {
        assertThrows(IllegalArgumentException.class,
                () -> library.addBook(new Book(isbn, "Title", "Author")));
    }

    @ParameterizedTest(name = "invalid title [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void addBookWithInvalidTitleThrows(String title) {
        assertThrows(IllegalArgumentException.class,
                () -> library.addBook(new Book("222", title, "Author")));
    }

    @ParameterizedTest(name = "invalid member id [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void borrowWithInvalidMemberIdThrows(String memberId) {
        assertThrows(IllegalArgumentException.class, () -> library.borrowBook("111", memberId));
        assertFalse(library.findByIsbn("111").isBorrowed());
    }

    @Test
    void findUnknownIsbnReturnsNull() {
        assertNull(library.findByIsbn("does-not-exist"));
    }
}