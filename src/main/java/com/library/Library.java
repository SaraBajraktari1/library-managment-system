package com.library;

import java.util.HashMap;
import java.util.Map;

public class Library {
    private final Map<String, Book> books = new HashMap<>();

    public void addBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book cannot be null");
        if (book.getIsbn() == null || book.getIsbn().isBlank())
            throw new IllegalArgumentException("ISBN is required");
        if (book.getTitle() == null || book.getTitle().isBlank())
            throw new IllegalArgumentException("Title is required");
        if (books.containsKey(book.getIsbn()))
            throw new IllegalStateException("Book with this ISBN already exists");
        books.put(book.getIsbn(), book);
    }

    public Book findByIsbn(String isbn) { return books.get(isbn); }

    public int getBookCount() { return books.size(); }
}