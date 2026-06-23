package com.capitec.fraud.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionEventRequest(
        @NotBlank String transactionReference,
        @NotBlank String customerId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotBlank String category,
        @NotBlank String merchant,
        @NotBlank @Size(min = 2, max = 2) String countryCode,
        @NotBlank String channel,
        @NotNull Instant transactionTime
) {}
