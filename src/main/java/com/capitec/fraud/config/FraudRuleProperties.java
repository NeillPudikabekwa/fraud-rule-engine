package com.capitec.fraud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Set;

@ConfigurationProperties(prefix = "fraud.rules")
public record FraudRuleProperties(
        BigDecimal highAmountThreshold,
        int velocityWindowMinutes,
        int velocityTransactionLimit,
        Set<String> highRiskCountries
) {}
