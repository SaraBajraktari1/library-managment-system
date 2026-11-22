package com.library;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private static String json(String isbn, String member, int days) {
        return String.format(
                "{\"bookIsbn\":\"%s\",\"memberId\":\"%s\",\"daysBorrowed\":%d}", isbn, member, days);
    }

    private long create(String isbn, String member, int days) throws Exception {
        String body = mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(isbn, member, days)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @Test
    void fullLifecycleCreateReadUpdateDeleteIsPersistedInDatabase() throws Exception {
        // Create: stored in H2 with the fee calculated by FeeCalculator
        long id = create("111", "M001", 20);
        Transaction saved = repository.findById(id).orElseThrow();
        assertEquals("111", saved.getBookIsbn());
        assertEquals(3.0, saved.getFee(), 0.001);

        // Read
        mockMvc.perform(get("/api/transactions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value("M001"));

        // Update: the database row changes and the fee is recalculated (capped at 10)
        mockMvc.perform(put("/api/transactions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("222", "M002", 40)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(10.0));
        Transaction updated = repository.findById(id).orElseThrow();
        assertEquals("222", updated.getBookIsbn());
        assertEquals("M002", updated.getMemberId());
        assertEquals(10.0, updated.getFee(), 0.001);

        // Delete: removed from the database and no longer reachable through the API
        mockMvc.perform(delete("/api/transactions/" + id))
                .andExpect(status().isNoContent());
        assertFalse(repository.existsById(id));
        mockMvc.perform(get("/api/transactions/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsStoredDataAndInvalidRequestsAreNotStored() throws Exception {
        create("111", "M001", 0);
        create("222", "M001", 14);
        create("333", "M002", 15);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        assertEquals(3, repository.count());
        assertEquals(2, repository.findByMemberId("M001").size());

        // Boundary values: 14 days is free, 15 days costs the first 0.5
        assertEquals(0.0, repository.findByMemberId("M001").get(1).getFee(), 0.001);
        assertEquals(0.5, repository.findByMemberId("M002").get(0).getFee(), 0.001);

        // Invalid input is rejected and nothing is saved
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("444", "M003", -5)))
                .andExpect(status().isBadRequest());
        assertEquals(3, repository.count());

        // Unknown id: 404 and the database is unchanged
        mockMvc.perform(put("/api/transactions/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("555", "M004", 5)))
                .andExpect(status().isNotFound());
        assertEquals(3, repository.count());
    }
}