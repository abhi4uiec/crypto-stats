package com.test.crypto.service;

import com.test.crypto.exception.FileMissingException;
import com.test.crypto.exception.FileParseException;
import com.test.crypto.exception.RecordMissingInCsvException;
import com.test.crypto.model.HighestNormalizedData;
import com.test.crypto.model.PriceData;
import com.test.crypto.model.Statistics;
import com.test.crypto.util.CurrencyValidator;
import com.test.crypto.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RefreshScope
public class CsvProcessor {

    @Value("${supported}")
    private String supportedCurrency;

    @Value("${currency.file.suffix}")
    private String fileSuffix;

    @Value("${currency.file.directory}")
    private String directory;

    private static final String FORWARD_SLASH = "/";

    private static final String EMPTY_STR = "";

    /**
     * Returns the oldest/newest/min/max values for a requested crypto
     * @param currency
     * @return Statistics
     */
    public Statistics gatherStatistics(final String currency) {
        log.debug("Processing currency {}", currency);

        // check if the currency is supported or not
        CurrencyValidator.validateCurrency(currency, supportedCurrency);
        
        List<PriceData> priceDataArrayList = processCsv(currency);

        var minPrice = priceDataArrayList.stream().min(Comparator.comparing(PriceData::price));
        var maxPrice = priceDataArrayList.stream().max(Comparator.comparing(PriceData::price));
        var oldestPrice = priceDataArrayList.stream().min(Comparator.comparing(PriceData::localDate));
        var latestPrice = priceDataArrayList.stream().max(Comparator.comparing(PriceData::localDate));

        return new Statistics(oldestPrice.get().price(),
                latestPrice.get().price(),
                minPrice.get().price(),
                maxPrice.get().price());
    }

    /**
     * Returns the crypto with the highest normalized range for a specific day
     * @param date
     * @return HighestNormalizedData
     */
    public HighestNormalizedData findHighestNormalizedCrypto(final String date) {

        DateUtil.validateDate(date);

        BigDecimal maxValue = new BigDecimal(0);
        String currency = EMPTY_STR;
        String fileName;
        List<PriceData> priceDataArrayList;
        String[] currencies = supportedCurrency.split(",");

        for (String curr : currencies) {
            fileName = curr + fileSuffix;

            log.debug("Processing file {} for the selected date {}", fileName, date);

            priceDataArrayList = processCsv(curr);

            Map<String, HighestNormalizedData> map = getHighestNormalizedData(date, priceDataArrayList);

            if (!map.containsKey(date)) {
                throw new RecordMissingInCsvException("No record found in csv " + fileName + " for date " + date);
            }
            BigDecimal maxNormalizedValue = map.get(date).normalizedRange();
            if (maxValue.compareTo(maxNormalizedValue) <= 0) {
                maxValue = maxNormalizedValue;
                currency = map.get(date).currency();
            }
        }
        return new HighestNormalizedData(maxValue, currency);
    }

    /**
     * Return a descending sorted list of all the cryptos, comparing the normalized range
     * @return list of HighestNormalizedData
     */
    public List<HighestNormalizedData> sortedNormalizedList() {

        List<HighestNormalizedData> list = new ArrayList<>();
        List<PriceData> priceDataArrayList;
        String fileName;
        String[] currencies = supportedCurrency.split(",");

        for (String curr : currencies) {

            fileName = curr + fileSuffix;

            log.debug("Processing file {}", fileName);

            priceDataArrayList = processCsv(curr);

            var min = priceDataArrayList.stream().min(Comparator.comparing(PriceData::price));
            var max = priceDataArrayList.stream().max(Comparator.comparing(PriceData::price));

            BigDecimal normalizedValue = (max.get().price().subtract(min.get().price())).divide(min.get().price(), 2, RoundingMode.HALF_UP);
            list.add(new HighestNormalizedData(normalizedValue, priceDataArrayList.get(0).currency()));
        }
        list.sort(Comparator.comparing(HighestNormalizedData::normalizedRange).reversed());
        return list;
    }

    /**
     * Gets the highest normalized cryptocurrency in the list
     * @param date
     * @param priceDataArrayList
     * @return
     */
    private Map<String, HighestNormalizedData> getHighestNormalizedData(final String date, final List<PriceData> priceDataArrayList) {
        var min = priceDataArrayList.stream().min(Comparator.comparing(PriceData::price));
        var max = priceDataArrayList.stream().max(Comparator.comparing(PriceData::price));

        Map<String, HighestNormalizedData> highestNormalizedMap = new HashMap<>();

        for (PriceData priceData : priceDataArrayList) {
            var dateKey = priceData.localDate().toLocalDate().toString();
            if (date.equals(dateKey)) {
                var normalizedPrice = normalize(priceData.price(), min.get().price(), max.get().price());
                if (highestNormalizedMap.containsKey(dateKey)) {
                    if (highestNormalizedMap.get(dateKey).normalizedRange().compareTo(normalizedPrice) < 0) {
                        highestNormalizedMap.put(dateKey, new HighestNormalizedData(normalizedPrice, priceData.currency()));
                    }
                } else {
                    highestNormalizedMap.put(dateKey, new HighestNormalizedData(normalizedPrice, priceData.currency()));
                }
            }
        }
        return highestNormalizedMap;
    }

    private List<PriceData> processCsv(final String currencyName) {

        String fileName = currencyName + fileSuffix;
        InputStream filePath = getClass().getResourceAsStream(directory + FORWARD_SLASH + fileName);

        if (null == filePath) {
            throw new FileMissingException("File missing = " + fileName);
        }

        List<PriceData> priceDataArrayList = new ArrayList<>();

        try (BufferedReader b = new BufferedReader(new InputStreamReader(filePath))) {
            String readLine;
            String[] namesString;
            b.readLine();

            while ((readLine = b.readLine()) != null) {
                namesString = readLine.split(",");

                var localDateTime =
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(namesString[0])
                        ), ZoneOffset.UTC);

                //add data to model
                priceDataArrayList.add(new PriceData(localDateTime,
                        currencyName,
                        new BigDecimal(namesString[2])));
            }
        } catch (IOException e) {
            throw new FileParseException("Error occurred while reading csv file " + currencyName + fileSuffix);
        }
        return priceDataArrayList;
    }

    /**
     * Calculates normalized range for each price
     * @param inValue
     * @param min
     * @param max
     * @return
     */
    public BigDecimal normalize(final BigDecimal inValue, final BigDecimal min, final BigDecimal max) {
        return (inValue.subtract(min)).divide(max.subtract(min), 2, RoundingMode.HALF_UP);
    }
}
