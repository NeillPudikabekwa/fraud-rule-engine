package com.capitec.fraud.dto;

public record FraudSummaryResponse(
        long totalTransactionsEvaluated,
        long totalFlagged,
        long highRiskOrCritical,
        double flaggedPercentage
) {}
