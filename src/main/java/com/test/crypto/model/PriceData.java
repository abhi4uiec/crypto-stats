package com.test.crypto.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceData(LocalDateTime localDate, String currency, BigDecimal price) {
}
