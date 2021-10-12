package org.innovateuk.ifs.crm.transactional;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeMachine {

    private static Clock clock = Clock.systemDefaultZone();
    private static ZoneId zoneId = ZoneId.systemDefault();

    public static ZonedDateTime now() {
        return ZonedDateTime.now(getClock());
    }

    public static void useFixedClockAt(ZonedDateTime date) {
        clock = Clock.fixed(date.toInstant(), zoneId);
    }

    private static Clock getClock() {
        return clock;
    }
}
