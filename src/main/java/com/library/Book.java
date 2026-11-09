package com.library;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private boolean borrowed;
    private String borrowedBy;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isBorrowed() { return borrowed; }
    public String getBorrowedBy() { return borrowedBy; }

    void markBorrowed(String memberId) {
        if (borrowed) throw new IllegalStateException("Book is already borrowed");
        borrowed = true;
        borrowedBy = memberId;
    }

    void markReturned() {
        if (!borrowed) throw new IllegalStateException("Book is not borrowed");
        borrowed = false;
        borrowedBy = null;
    }
}