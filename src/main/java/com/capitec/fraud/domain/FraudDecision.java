package com.capitec.fraud.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fraud_decisions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private TransactionEvent transactionEvent;

    @Column(nullable = false)
    private boolean flagged;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "fraud_decision_reasons", joinColumns = @JoinColumn(name = "decision_id"))
    @Column(name = "reason", nullable = false)
    @Builder.Default
    private List<String> reasons = new ArrayList<>();

    @Column(nullable = false)
    private Instant evaluatedAt;
}
