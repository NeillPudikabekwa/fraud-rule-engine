package com.capitec.fraud.rule;

import com.capitec.fraud.config.FraudRuleProperties;
import com.capitec.fraud.domain.TransactionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HighAmountRule implements FraudRule {
    private final FraudRuleProperties properties;

    public HighAmountRule(FraudRuleProperties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<String> evaluate(TransactionEvent transactionEvent) {
        if (transactionEvent.getAmount().compareTo(properties.highAmountThreshold()) >= 0) {
            return Optional.of("HIGH_AMOUNT: transaction amount is greater than or equal to configured threshold");
        }
        return Optional.empty();
    }
}
