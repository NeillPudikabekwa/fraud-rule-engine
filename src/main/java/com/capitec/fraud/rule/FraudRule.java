package com.capitec.fraud.rule;

import com.capitec.fraud.domain.TransactionEvent;

import java.util.Optional;

public interface FraudRule {
    Optional<String> evaluate(TransactionEvent transactionEvent);
}
