package com.capitec.fraud.dto;

import com.capitec.fraud.domain.RiskLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FraudDecisionResponse(
        UUID decisionId,
        String transactionReference,
        String customerId,
        BigDecimal amount,
        String currency,
        String category,
        String merchant,
        String countryCode,
        String channel,
        boolean flagged,
        RiskLevel riskLevel,
        List<String> reasons,
        Instant transactionTime,
        Instant evaluatedAt
) {}
