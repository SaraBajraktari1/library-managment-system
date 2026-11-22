package com.library;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookIsbn;

    @Column(nullable = false)
    private String memberId;

    private int daysBorrowed;

    private double fee;

    protected Transaction() {
    }

    public Transaction(String bookIsbn, String memberId, int daysBorrowed) {
        update(bookIsbn, memberId, daysBorrowed);
    }

    public void update(String bookIsbn, String memberId, int daysBorrowed) {
        this.bookIsbn = bookIsbn;
        this.memberId = memberId;
        this.daysBorrowed = daysBorrowed;
        this.fee = FeeCalculator.calculateFee(daysBorrowed);
    }

    public Long getId() { return id; }
    public String getBookIsbn() { return bookIsbn; }
    public String getMemberId() { return memberId; }
    public int getDaysBorrowed() { return daysBorrowed; }
    public double getFee() { return fee; }
}