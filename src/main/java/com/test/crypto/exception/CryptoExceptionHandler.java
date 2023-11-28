package com.test.crypto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Methods annotated with @ExceptionHandler are shared globally across multiple @Controller components
 * to capture exceptions and translate them to HTTP responses
 */
@RestControllerAdvice
public class CryptoExceptionHandler {

    @ExceptionHandler(UnsupportedCurrencyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity handleUnsupportedCurrency(final UnsupportedCurrencyException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(FileMissingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity handleFileNotFoundException(final FileMissingException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(FileParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity handleFileReadingException(final FileParseException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity handleInvalidDateException(final InvalidDateException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(RecordMissingInCsvException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity handleRecordMissingInCsvException(final RecordMissingInCsvException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(response.status()).body(response);
    }

}
