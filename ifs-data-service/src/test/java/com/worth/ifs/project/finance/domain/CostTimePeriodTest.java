package com.worth.ifs.project.finance.domain;

import com.worth.ifs.project.finance.resource.TimeUnit;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.worth.ifs.project.finance.resource.TimeUnit.*;
import static org.junit.Assert.assertEquals;

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
        assertEquals(LocalDateTime.of(2016, 8, 14, 1, 2, 3), period.getStartDateTime(LocalDateTime.of(2016, 8, 12, 1, 2, 3)));

        // assert the end date takes into account the offsetAmount, offsetUnit, durationAmount and durationUnit
        assertEquals(LocalDateTime.of(2020, 8, 14, 1, 2, 3), period.getEndDateTime(LocalDateTime.of(2016, 8, 12, 1, 2, 3)));
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
        assertEquals(LocalDateTime.of(2016, 2, 29, 1, 2, 3), period.getEndDateTime(LocalDateTime.of(2016, 1, 31, 1, 2, 3)));

        // going from the last day of February (29th) forward one month should land us on the 29th March as there's no
        // truncation going from Feb to March
        assertEquals(LocalDateTime.of(2016, 3, 29, 1, 2, 3), period.getEndDateTime(LocalDateTime.of(2016, 2, 29, 1, 2, 3)));
    }
}
