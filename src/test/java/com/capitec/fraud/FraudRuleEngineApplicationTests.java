package com.capitec.fraud;

import com.capitec.fraud.dto.TransactionEventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FraudRuleEngineApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void highAmountTransactionIsFlagged() throws Exception {
        TransactionEventRequest request = new TransactionEventRequest(
                "TXN-100001",
                "CUST-001",
                new BigDecimal("75000.00"),
                "ZAR",
                "TRANSFER",
                "Online Banking",
                "ZA",
                "MOBILE_APP",
                Instant.parse("2026-06-17T08:00:00Z")
        );

        mockMvc.perform(post("/api/v1/transactions/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.reasons[0]").value(containsString("HIGH_AMOUNT")));
    }

    @Test
    void duplicateTransactionReferenceReturnsConflict() throws Exception {
        TransactionEventRequest request = new TransactionEventRequest(
                "TXN-100002",
                "CUST-002",
                new BigDecimal("120.00"),
                "ZAR",
                "CARD_PAYMENT",
                "Coffee Shop",
                "ZA",
                "CARD",
                Instant.parse("2026-06-17T09:00:00Z")
        );

        String body = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/v1/transactions/evaluate").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/transactions/evaluate").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void summaryEndpointReturnsMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/fraud-decisions/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransactionsEvaluated").exists())
                .andExpect(jsonPath("$.totalFlagged").exists());
    }
}
