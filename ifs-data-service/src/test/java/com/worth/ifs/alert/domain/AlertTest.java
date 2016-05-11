package com.worth.ifs.alert.domain;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.worth.ifs.alert.domain.AlertType.MAINTENANCE;
import static org.junit.Assert.assertEquals;

public class AlertTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_alertShouldReturnCorrectAttributeValuesAfterConstructors() throws Exception {
        final String message = "Sample message";
        final AlertType type = MAINTENANCE;
        final LocalDateTime validFromDate = LocalDateTime.parse("2016-05-06T21:00:00.00");
        final LocalDateTime validToDate = LocalDateTime.parse("2016-05-06T21:05:00.00");

        final Alert alert = new Alert(message, type, validFromDate, validToDate);

        assertEquals(message, alert.getMessage());
        assertEquals(type, alert.getType());
        assertEquals(validFromDate, alert.getValidFromDate());
        assertEquals(validToDate, alert.getValidToDate());
    }

    @Test
    public void test_alertShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        final Long id = 9999L;
        final String message = "Sample message";
        final AlertType type = MAINTENANCE;
        final LocalDateTime validFromDate = LocalDateTime.parse("2016-05-06T21:00:00.00");
        final LocalDateTime validToDate = LocalDateTime.parse("2016-05-06T21:05:00.00");

        final Alert alert = new Alert();

        alert.setId(id);
        alert.setMessage(message);
        alert.setType(type);
        alert.setValidFromDate(validFromDate);
        alert.setValidToDate(validToDate);

        assertEquals(id, alert.getId());
        assertEquals(message, alert.getMessage());
        assertEquals(type, alert.getType());
        assertEquals(validFromDate, alert.getValidFromDate());
        assertEquals(validToDate, alert.getValidToDate());
    }
}
