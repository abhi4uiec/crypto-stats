package com.test.crypto.util;

import com.test.crypto.exception.InvalidDateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    public static void validateDate(final String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException("Date provided is invalid and must be in format YYYY-MM-dd = " + date);
        }
    }
}
