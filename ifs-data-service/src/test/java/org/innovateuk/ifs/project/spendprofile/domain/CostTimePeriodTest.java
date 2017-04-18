package org.innovateuk.ifs.project.spendprofile.domain;

import org.innovateuk.ifs.project.finance.resource.TimeUnit;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.project.finance.resource.TimeUnit.*;
import static org.junit.Assert.*;

public class CostTimePeriodTest {

    @Test
    public void testCostTimePeriodStartAndEndDates() {

        int offsetAmount = 2;
        TimeUnit offsetUnit = DAY;
        int durationAmount = 4;
        TimeUnit durationUnit = YEAR;

        CostTimePeriod period = new CostTimePeriod(new Cost(), offsetAmount, offsetUnit, durationAmount, durationUnit);

        // assert the start date takes into account the offsetAmount and offsetUnit
        assertEquals(LocalDate.of(2016, 8, 14), period.getStartDate(LocalDate.of(2016, 8, 12)));

        // assert the end date takes into account the offsetAmount, offsetUnit, durationAmount and durationUnit
        assertEquals(LocalDate.of(2020, 8, 14), period.getEndDate(LocalDate.of(2016, 8, 12)));
    }

    @Test
    public void testCostTimePeriodEndDatesAtEndOfMonth() {

        int offsetAmount = 0;
        TimeUnit offsetUnit = DAY;
        int durationAmount = 1;
        TimeUnit durationUnit = MONTH;

        CostTimePeriod period = new CostTimePeriod(new Cost(), offsetAmount, offsetUnit, durationAmount, durationUnit);

        // going from the last day of January (31st) forward one month should land us on the last day of February (29th)
        // rather than wrapping around into March
        assertEquals(LocalDate.of(2016, 2, 29), period.getEndDate(LocalDate.of(2016, 1, 31)));

        // going from the last day of February (29th) forward one month should land us on the 29th March as there's no
        // truncation going from Feb to March
        assertEquals(LocalDate.of(2016, 3, 29), period.getEndDate(LocalDate.of(2016, 2, 29)));
    }

    @Test
    public void testCostTimePeriodStartAndEndDateTimes() {

        int offsetAmount = 2;
        TimeUnit offsetUnit = DAY;
        int durationAmount = 4;
        TimeUnit durationUnit = YEAR;

        CostTimePeriod period = new CostTimePeriod(new Cost(), offsetAmount, offsetUnit, durationAmount, durationUnit);

        // assert the start date takes into account the offsetAmount and offsetUnit
        assertEquals(ZonedDateTime.of(2016, 8, 14, 1, 2, 3, 0, ZoneId.systemDefault()), period.getStartDateTime(ZonedDateTime.of(2016, 8, 12, 1, 2, 3, 0, ZoneId.systemDefault())));

        // assert the end date takes into account the offsetAmount, offsetUnit, durationAmount and durationUnit
        assertEquals(ZonedDateTime.of(2020, 8, 14, 1, 2, 0, 0, ZoneId.systemDefault()), period.getEndDateTime(ZonedDateTime.of(2016, 8, 12, 1, 2, 0, 0, ZoneId.systemDefault())));
    }

    @Test
    public void testCostTimePeriodEndDateTimesAtEndOfMonth() {

        int offsetAmount = 0;
        TimeUnit offsetUnit = DAY;
        int durationAmount = 1;
        TimeUnit durationUnit = MONTH;

        CostTimePeriod period = new CostTimePeriod(new Cost(), offsetAmount, offsetUnit, durationAmount, durationUnit);

        // going from the last day of January (31st) forward one month should land us on the last day of February (29th)
        // rather than wrapping around into March
        assertEquals(ZonedDateTime.of(2016, 2, 29, 1, 2, 3, 0, ZoneId.systemDefault()), period.getEndDateTime(ZonedDateTime.of(2016, 1, 31, 1, 2, 3, 0, ZoneId.systemDefault())));

        // going from the last day of February (29th) forward one month should land us on the 29th March as there's no
        // truncation going from Feb to March
        assertEquals(ZonedDateTime.of(2016, 3, 29, 1, 2, 3, 0, ZoneId.systemDefault()), period.getEndDateTime(ZonedDateTime.of(2016, 2, 29, 1, 2, 3, 0, ZoneId.systemDefault())));
    }
}
