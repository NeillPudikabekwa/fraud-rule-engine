package com.capitec.fraud.rule;

import com.capitec.fraud.config.FraudRuleProperties;
import com.capitec.fraud.domain.TransactionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HighRiskCountryRule implements FraudRule {
    private final FraudRuleProperties properties;

    public HighRiskCountryRule(FraudRuleProperties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<String> evaluate(TransactionEvent transactionEvent) {
        String country = transactionEvent.getCountryCode().toUpperCase();
        if (properties.highRiskCountries().contains(country)) {
            return Optional.of("HIGH_RISK_COUNTRY: transaction originated from a configured high-risk country");
        }
        return Optional.empty();
    }
}
