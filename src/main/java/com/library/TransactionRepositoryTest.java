package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @Test
    void saveAssignsIdAndStoresData() {
        Transaction saved = repository.save(new Transaction("111", "M001", 20));

        assertNotNull(saved.getId());
        Transaction found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("111", found.getBookIsbn());
        assertEquals("M001", found.getMemberId());
        assertEquals(20, found.getDaysBorrowed());
        assertEquals(3.0, found.getFee(), 0.001);
    }

    @Test
    void findAllReturnsAllSavedTransactions() {
        repository.save(new Transaction("111", "M001", 5));
        repository.save(new Transaction("222", "M002", 15));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void findByIdOfUnknownIdIsEmpty() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void updateChangesAreStored() {
        Transaction saved = repository.save(new Transaction("111", "M001", 5));

        saved.update("222", "M002", 40);
        repository.saveAndFlush(saved);

        Transaction found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("222", found.getBookIsbn());
        assertEquals("M002", found.getMemberId());
        assertEquals(10.0, found.getFee(), 0.001);
    }

    @Test
    void deleteRemovesTransaction() {
        Transaction saved = repository.save(new Transaction("111", "M001", 5));

        repository.deleteById(saved.getId());

        assertFalse(repository.existsById(saved.getId()));
        assertEquals(0, repository.count());
    }

    @Test
    void findByMemberIdReturnsOnlyThatMembersTransactions() {
        repository.save(new Transaction("111", "M001", 5));
        repository.save(new Transaction("222", "M001", 6));
        repository.save(new Transaction("333", "M002", 7));

        List<Transaction> result = repository.findByMemberId("M001");

        assertEquals(2, result.size());
    }
}