package com.capitec.fraud.repository;

import com.capitec.fraud.domain.FraudDecision;
import com.capitec.fraud.domain.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FraudDecisionRepository extends JpaRepository<FraudDecision, UUID> {
    Page<FraudDecision> findByFlagged(boolean flagged, Pageable pageable);
    long countByFlagged(boolean flagged);
    long countByRiskLevelIn(Iterable<RiskLevel> levels);

    @Query("select d from FraudDecision d where d.transactionEvent.customerId = :customerId")
    Page<FraudDecision> findByCustomerId(String customerId, Pageable pageable);
}
