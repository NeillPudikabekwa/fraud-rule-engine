package com.capitec.fraud.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transaction_events", indexes = {
        @Index(name = "idx_transaction_customer_time", columnList = "customerId,transactionTime"),
        @Index(name = "idx_transaction_reference", columnList = "transactionReference", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String transactionReference;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String merchant;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private Instant transactionTime;

    @Column(nullable = false)
    private Instant receivedAt;
}
