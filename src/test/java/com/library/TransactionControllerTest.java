package com.library;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;

    private static Transaction tx(long id, String isbn, String member, int days) {
        Transaction t = new Transaction(isbn, member, days);
        ReflectionTestUtils.setField(t, "id", id);
        return t;
    }

    private static String json(String isbn, String member, int days) {
        return String.format(
                "{\"bookIsbn\":\"%s\",\"memberId\":\"%s\",\"daysBorrowed\":%d}", isbn, member, days);
    }

    // ---------- Create ----------

    @Test
    void createTransactionReturns201AndBody() throws Exception {
        when(service.create(any(TransactionRequest.class))).thenReturn(tx(1L, "111", "M001", 20));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("111", "M001", 20)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bookIsbn").value("111"))
                .andExpect(jsonPath("$.memberId").value("M001"))
                .andExpect(jsonPath("$.fee").value(3.0));
    }

    @Test
    void createTransactionWithZeroDaysIsValidBoundary() throws Exception {
        when(service.create(any(TransactionRequest.class))).thenReturn(tx(2L, "111", "M001", 0));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("111", "M001", 0)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fee").value(0.0));
    }

    @Test
    void createTransactionWithNegativeDaysReturns400() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("111", "M001", -5)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(service, never()).create(any());
    }

    @ParameterizedTest(name = "invalid body #{index}")
    @ValueSource(strings = {
            "{\"bookIsbn\":\"\",\"memberId\":\"M001\",\"daysBorrowed\":5}",
            "{\"bookIsbn\":\"111\",\"memberId\":\" \",\"daysBorrowed\":5}",
            "{\"memberId\":\"M001\",\"daysBorrowed\":5}",
            "{\"bookIsbn\":\"111\",\"memberId\":\"M001\",\"daysBorrowed\":-1}"
    })
    void createTransactionWithInvalidBodyReturns400(String body) throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }

    // ---------- Read ----------

    @Test
    void getAllTransactionsReturnsList() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                tx(1L, "111", "M001", 5),
                tx(2L, "222", "M002", 15)));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].bookIsbn").value("111"))
                .andExpect(jsonPath("$[1].fee").value(0.5));
    }

    @Test
    void getAllTransactionsReturnsEmptyListWhenNoData() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTransactionByIdReturnsTransaction() throws Exception {
        when(service.findById(1L)).thenReturn(tx(1L, "111", "M001", 20));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.memberId").value("M001"));
    }

    @Test
    void getTransactionByUnknownIdReturns404() throws Exception {
        when(service.findById(99L)).thenThrow(new TransactionNotFoundException(99L));

        mockMvc.perform(get("/api/transactions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found: 99"));
    }
        // ---------- Update ----------

    @Test
    void updateTransactionReturns200AndNewValues() throws Exception {
        when(service.update(eq(1L), any(TransactionRequest.class)))
                .thenReturn(tx(1L, "222", "M002", 40));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("222", "M002", 40)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookIsbn").value("222"))
                .andExpect(jsonPath("$.memberId").value("M002"))
                .andExpect(jsonPath("$.fee").value(10.0));
    }

    @Test
    void updateTransactionWithNegativeDaysReturns400() throws Exception {
        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("222", "M002", -1)))
                .andExpect(status().isBadRequest());

        verify(service, never()).update(anyLong(), any());
    }

    @Test
    void updateUnknownTransactionReturns404() throws Exception {
        when(service.update(eq(99L), any(TransactionRequest.class)))
                .thenThrow(new TransactionNotFoundException(99L));

        mockMvc.perform(put("/api/transactions/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("222", "M002", 5)))
                .andExpect(status().isNotFound());
    }

    // ---------- Delete ----------

    @Test
    void deleteTransactionReturns204() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void deleteUnknownTransactionReturns404() throws Exception {
        doThrow(new TransactionNotFoundException(99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/transactions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found: 99"));
    }
        private org.springframework.test.web.servlet.ResultActions sendJson(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder, String body)
            throws Exception {
        return mockMvc.perform(builder.contentType(MediaType.APPLICATION_JSON).content(body));
    }
}