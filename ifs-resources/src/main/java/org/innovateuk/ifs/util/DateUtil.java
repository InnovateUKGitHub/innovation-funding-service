package org.innovateuk.ifs.util;

import java.time.LocalDate;

public final class DateUtil {

    public static boolean isFutureDate(LocalDate value) {
        if(value == null) {
            return false;
        }

        LocalDate today = LocalDate.now();

        return value.isAfter(today);
    }
}
