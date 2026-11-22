package com.library;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction create(TransactionRequest request) {
        return repository.save(
                new Transaction(request.bookIsbn(), request.memberId(), request.daysBorrowed()));
    }

    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }
        public Transaction update(Long id, TransactionRequest request) {
        Transaction transaction = findById(id);
        transaction.update(request.bookIsbn(), request.memberId(), request.daysBorrowed());
        return repository.save(transaction);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}