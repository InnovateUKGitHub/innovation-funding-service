package org.innovateuk.ifs.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeZoneUtil {

    public static final ZoneId UK_TIME_ZONE = ZoneId.of("Europe/London");

    public static ZonedDateTime toUkTimeZone(ZonedDateTime zonedDateTime) {
        if (zonedDateTime != null) {
            return zonedDateTime.withZoneSameInstant(UK_TIME_ZONE);
        }
        return null;
    }

    public static ZonedDateTime fromUkTimeZone(int year, int month, int day) {
        return fromUkTimeZone(year, month, day, 0);
    }

    public static ZonedDateTime fromUkTimeZone(int year, int month, int day, int hours) {
        return ZonedDateTime.of(year, month, day, hours, 0, 0, 0, UK_TIME_ZONE);

    }

}
