package com.test.crypto.util;

import com.test.crypto.exception.UnsupportedCurrencyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrencyValidator {

    /**
     * For unsupported cryptocurrency throw exception
     * @param currency
     * @param supportedCurrency
     */
    public static void validateCurrency(final String currency, final String supportedCurrency) {
        if (!supportedCurrency.contains(currency)) {
            log.info("Currency {} not supported", currency);
            throw new UnsupportedCurrencyException("Currency not supported");
        }
    }

}
