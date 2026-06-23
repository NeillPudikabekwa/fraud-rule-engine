package com.capitec.fraud.service;

import com.capitec.fraud.domain.FraudDecision;
import com.capitec.fraud.domain.RiskLevel;
import com.capitec.fraud.domain.TransactionEvent;
import com.capitec.fraud.dto.FraudDecisionResponse;
import com.capitec.fraud.dto.FraudSummaryResponse;
import com.capitec.fraud.dto.TransactionEventRequest;
import com.capitec.fraud.exception.DuplicateTransactionException;
import com.capitec.fraud.repository.FraudDecisionRepository;
import com.capitec.fraud.repository.TransactionEventRepository;
import com.capitec.fraud.rule.FraudRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FraudEvaluationService {
    private final TransactionEventRepository transactionEventRepository;
    private final FraudDecisionRepository fraudDecisionRepository;
    private final List<FraudRule> fraudRules;

    public FraudEvaluationService(TransactionEventRepository transactionEventRepository,
                                  FraudDecisionRepository fraudDecisionRepository,
                                  List<FraudRule> fraudRules) {
        this.transactionEventRepository = transactionEventRepository;
        this.fraudDecisionRepository = fraudDecisionRepository;
        this.fraudRules = fraudRules;
    }

    @Transactional
    public FraudDecisionResponse process(TransactionEventRequest request) {
        if (transactionEventRepository.existsByTransactionReference(request.transactionReference())) {
            throw new DuplicateTransactionException(request.transactionReference());
        }

        TransactionEvent event = TransactionEvent.builder()
                .transactionReference(request.transactionReference())
                .customerId(request.customerId())
                .amount(request.amount())
                .currency(request.currency().toUpperCase())
                .category(request.category())
                .merchant(request.merchant())
                .countryCode(request.countryCode().toUpperCase())
                .channel(request.channel())
                .transactionTime(request.transactionTime())
                .receivedAt(Instant.now())
                .build();

        List<String> reasons = fraudRules.stream()
                .map(rule -> rule.evaluate(event))
                .flatMap(java.util.Optional::stream)
                .toList();

        FraudDecision decision = FraudDecision.builder()
                .transactionEvent(event)
                .flagged(!reasons.isEmpty())
                .riskLevel(resolveRiskLevel(reasons.size()))
                .reasons(reasons)
                .evaluatedAt(Instant.now())
                .build();

        return toResponse(fraudDecisionRepository.save(decision));
    }

    @Transactional(readOnly = true)
    public FraudDecisionResponse getDecision(UUID id) {
        return fraudDecisionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Fraud decision not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<FraudDecisionResponse> list(Boolean flagged, String customerId, Pageable pageable) {
        Page<FraudDecision> page;
        if (customerId != null && !customerId.isBlank()) {
            page = fraudDecisionRepository.findByCustomerId(customerId, pageable);
        } else if (flagged != null) {
            page = fraudDecisionRepository.findByFlagged(flagged, pageable);
        } else {
            page = fraudDecisionRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public FraudSummaryResponse summary() {
        long total = fraudDecisionRepository.count();
        long flagged = fraudDecisionRepository.countByFlagged(true);
        long highRisk = fraudDecisionRepository.countByRiskLevelIn(List.of(RiskLevel.HIGH, RiskLevel.CRITICAL));
        double percentage = total == 0 ? 0 : (flagged * 100.0) / total;
        return new FraudSummaryResponse(total, flagged, highRisk, Math.round(percentage * 100.0) / 100.0);
    }

    private RiskLevel resolveRiskLevel(int reasonCount) {
        if (reasonCount >= 3) return RiskLevel.CRITICAL;
        if (reasonCount == 2) return RiskLevel.HIGH;
        if (reasonCount == 1) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private FraudDecisionResponse toResponse(FraudDecision decision) {
        TransactionEvent event = decision.getTransactionEvent();
        return new FraudDecisionResponse(
                decision.getId(),
                event.getTransactionReference(),
                event.getCustomerId(),
                event.getAmount(),
                event.getCurrency(),
                event.getCategory(),
                event.getMerchant(),
                event.getCountryCode(),
                event.getChannel(),
                decision.isFlagged(),
                decision.getRiskLevel(),
                decision.getReasons(),
                event.getTransactionTime(),
                decision.getEvaluatedAt()
        );
    }
}
