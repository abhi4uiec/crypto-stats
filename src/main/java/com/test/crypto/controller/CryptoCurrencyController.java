package com.test.crypto.controller;

import com.test.crypto.exception.FileMissingException;
import com.test.crypto.exception.FileParseException;
import com.test.crypto.exception.RecordMissingInCsvException;
import com.test.crypto.exception.UnsupportedCurrencyException;
import com.test.crypto.model.HighestNormalizedData;
import com.test.crypto.model.Statistics;
import com.test.crypto.service.CryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CryptoCurrencyController {

    private final CryptoService cryptoService;

    public CryptoCurrencyController(final CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Operation(summary = "Finds the oldest/newest/min/max values for a requested crypto")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics collected successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Statistics.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Currency not supported",
                    content = { @Content(schema = @Schema(oneOf = {
                            UnsupportedCurrencyException.class,
                            FileMissingException.class,
                            FileParseException.class
                    })) }) })
    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public Statistics fetchStatistics(
            @Parameter(description = "currency against which statistics will be collected")
            @RequestParam(name = "currency")
            final String currency) {

        return cryptoService.fetchStatistics(currency);
    }

    @Operation(summary = "Finds the crypto with the highest normalized range for a specific day")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Crypto with highest normalized range returned successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HighestNormalizedData.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "No record found for a specific date",
                    content = { @Content(schema = @Schema(oneOf = {
                            RecordMissingInCsvException.class,
                            FileMissingException.class,
                            FileParseException.class
                    })) }) })
    @GetMapping(value = "/max-normalized-range")
    @ResponseStatus(HttpStatus.OK)
    public HighestNormalizedData maxNormalizedCrypto(
            @Parameter(description = "specific day")
            @RequestParam(name = "date")
            final String date) {

        return cryptoService.findHighestNormalizedCrypto(date);
    }

    @Operation(summary = "Returns a descending sorted list of all the cryptos comparing the normalized range")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sorted list returned successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HighestNormalizedData.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "No record found for a specific date",
                    content = { @Content(schema = @Schema(oneOf = {
                            RecordMissingInCsvException.class,
                            FileMissingException.class,
                            FileParseException.class
                    })) }) })
    @GetMapping("/normalized-list")
    @ResponseStatus(HttpStatus.OK)
    public List<HighestNormalizedData> normalizedList() {

        return cryptoService.sortedNormalizedList();
    }

}
