package com.test.crypto.service;

import com.test.crypto.model.HighestNormalizedData;
import com.test.crypto.model.Statistics;

import java.util.List;

public interface CryptoService {

    /**
     * Returns the oldest/newest/min/max values for a requested crypto
     * @param currency
     * @return Statistics
     */
    Statistics fetchStatistics(String currency);

    /**
     * Returns the crypto with the highest normalized range for a specific day
     * @param date
     * @return HighestNormalizedData
     */
    HighestNormalizedData findHighestNormalizedCrypto(String date);

    /**
     * Return a descending sorted list of all the cryptos, comparing the normalized range
     * @return list of HighestNormalizedData
     */
    List<HighestNormalizedData> sortedNormalizedList();
}
