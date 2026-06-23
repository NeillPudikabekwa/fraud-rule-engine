package com.capitec.fraud.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String transactionReference) {
        super("Transaction reference already exists: " + transactionReference);
    }
}
