package com.capitec.fraud.repository;

import com.capitec.fraud.domain.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, UUID> {
    boolean existsByTransactionReference(String transactionReference);
    long countByCustomerIdAndTransactionTimeBetween(String customerId, Instant from, Instant to);
}
