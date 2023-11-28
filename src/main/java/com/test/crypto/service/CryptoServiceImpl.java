package com.test.crypto.service;

import com.test.crypto.model.HighestNormalizedData;
import com.test.crypto.model.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    private final CsvProcessor csvProcessor;

    public CryptoServiceImpl(final CsvProcessor csvProcessor) {
        this.csvProcessor = csvProcessor;
    }

    @Override
    public Statistics fetchStatistics(final String currency) {
        return csvProcessor.gatherStatistics(currency);
    }

    @Override
    public HighestNormalizedData findHighestNormalizedCrypto(final String date) {
        return csvProcessor.findHighestNormalizedCrypto(date);
    }

    @Override
    public List<HighestNormalizedData> sortedNormalizedList() {
        return csvProcessor.sortedNormalizedList();
    }

}
