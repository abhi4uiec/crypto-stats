package com.test.crypto.model;

import java.math.BigDecimal;

public record Statistics(BigDecimal oldestPrice, BigDecimal newestPrice, BigDecimal minPrice, BigDecimal maxPrice) {
}
