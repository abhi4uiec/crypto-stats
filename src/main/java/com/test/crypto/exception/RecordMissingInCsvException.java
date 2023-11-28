package com.test.crypto.exception;

public class RecordMissingInCsvException extends RuntimeException {

    public RecordMissingInCsvException(final String message) {
        super(message);
    }
}

