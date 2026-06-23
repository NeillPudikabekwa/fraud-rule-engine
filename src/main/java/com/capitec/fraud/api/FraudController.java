package com.capitec.fraud.api;

import com.capitec.fraud.dto.FraudDecisionResponse;
import com.capitec.fraud.dto.FraudSummaryResponse;
import com.capitec.fraud.dto.TransactionEventRequest;
import com.capitec.fraud.service.FraudEvaluationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class FraudController {
    private final FraudEvaluationService fraudEvaluationService;

    public FraudController(FraudEvaluationService fraudEvaluationService) {
        this.fraudEvaluationService = fraudEvaluationService;
    }

    @PostMapping("/transactions/evaluate")
    @ResponseStatus(HttpStatus.CREATED)
    public FraudDecisionResponse evaluate(@Valid @RequestBody TransactionEventRequest request) {
        return fraudEvaluationService.process(request);
    }

    @GetMapping("/fraud-decisions/{id}")
    public FraudDecisionResponse getDecision(@PathVariable UUID id) {
        return fraudEvaluationService.getDecision(id);
    }

    @GetMapping("/fraud-decisions")
    public Page<FraudDecisionResponse> listDecisions(@RequestParam(required = false) Boolean flagged,
                                                     @RequestParam(required = false) String customerId,
                                                     Pageable pageable) {
        return fraudEvaluationService.list(flagged, customerId, pageable);
    }

    @GetMapping("/fraud-decisions/summary")
    public FraudSummaryResponse summary() {
        return fraudEvaluationService.summary();
    }
}
