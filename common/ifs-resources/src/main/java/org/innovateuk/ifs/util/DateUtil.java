package org.innovateuk.ifs.util;

import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import static java.util.Optional.of;


public final class DateUtil {

    public static boolean isFutureDate(LocalDate value) {
        if(value == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        return value.isAfter(today);
    }

    public static String getNameOfDay(Integer day, Integer month, Integer year) {
        return getDayOfWeek(day, month, year)
                .map(DayOfWeek::name)
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .orElse("-");
    }

    public static Optional<DayOfWeek> getDayOfWeek(Integer day, Integer month, Integer year) {
        if (day != null && month != null && year != null) {
            try {
                return of(TimeZoneUtil.fromUkTimeZone(year, month, day).getDayOfWeek());
            } catch (DateTimeException e) {
                // We need to catch this as we may well call with invalid dates if the user has entered these.
            }
        }
        return Optional.empty();
    }
}
