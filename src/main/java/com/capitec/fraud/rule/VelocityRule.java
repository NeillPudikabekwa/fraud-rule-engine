package com.capitec.fraud.rule;

import com.capitec.fraud.config.FraudRuleProperties;
import com.capitec.fraud.domain.TransactionEvent;
import com.capitec.fraud.repository.TransactionEventRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class VelocityRule implements FraudRule {
    private final FraudRuleProperties properties;
    private final TransactionEventRepository transactionEventRepository;

    public VelocityRule(FraudRuleProperties properties, TransactionEventRepository transactionEventRepository) {
        this.properties = properties;
        this.transactionEventRepository = transactionEventRepository;
    }

    @Override
    public Optional<String> evaluate(TransactionEvent transactionEvent) {
        Instant from = transactionEvent.getTransactionTime().minusSeconds(properties.velocityWindowMinutes() * 60L);
        Instant to = transactionEvent.getTransactionTime();
        long previousTransactions = transactionEventRepository.countByCustomerIdAndTransactionTimeBetween(
                transactionEvent.getCustomerId(), from, to);
        if (previousTransactions >= properties.velocityTransactionLimit()) {
            return Optional.of("VELOCITY: customer exceeded transaction count within configured time window");
        }
        return Optional.empty();
    }
}
