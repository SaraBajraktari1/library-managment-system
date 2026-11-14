package com.library;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Library {
    public static final int MAX_LOANS = 3;

    private final Map<String, Book> books = new LinkedHashMap<>();

    public void addBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");
        requireText(book.getIsbn(), "ISBN");
        requireText(book.getTitle(), "Title");
        if (books.containsKey(book.getIsbn()))
            throw new IllegalStateException("Book with this ISBN already exists");
        books.put(book.getIsbn(), book);
    }

    public void borrowBook(String isbn, String memberId) {
        requireText(memberId, "Member ID");
        Book book = findBookOrThrow(isbn);
        if (getBorrowedCount(memberId) >= MAX_LOANS)
            throw new IllegalStateException("Borrow limit reached");
        book.markBorrowed(memberId);
    }

    public void returnBook(String isbn) {
        findBookOrThrow(isbn).markReturned();
    }

       public int getBorrowedCount(String memberId) {
        return (int) books.values().stream()
                .filter(book -> book.isBorrowed() && Objects.equals(book.getBorrowedBy(), memberId))
                .count();
    }
    public java.util.Collection<Book> getBooks() {
        return java.util.Collections.unmodifiableCollection(books.values());
    }
    public Book findByIsbn(String isbn) { return books.get(isbn); }

    public int getBookCount() { return books.size(); }

    private Book findBookOrThrow(String isbn) {
        Book book = books.get(isbn);
        if (book == null) throw new IllegalArgumentException("Book not found: " + isbn);
        return book;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " is required");
    }
}