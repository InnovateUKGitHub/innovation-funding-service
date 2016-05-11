package com.worth.ifs.alert.resource;

import com.worth.ifs.alert.domain.AlertType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.worth.ifs.alert.domain.AlertType.MAINTENANCE;
import static org.junit.Assert.assertEquals;

public class AlertResourceTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_alertResourceShouldReturnCorrectAttributeValuesAfterConstructors() throws Exception {
        final Long id = 9999L;
        final String message = "Sample message";
        final AlertType type = MAINTENANCE;
        final LocalDateTime validFromDate = LocalDateTime.parse("2016-05-06T21:00:00.00");
        final LocalDateTime validToDate = LocalDateTime.parse("2016-05-06T21:05:00.00");

        final AlertResource alertResource = new AlertResource(id, message, type, validFromDate, validToDate);

        assertEquals(id, alertResource.getId());
        assertEquals(message, alertResource.getMessage());
        assertEquals(type, alertResource.getType());
        assertEquals(validFromDate, alertResource.getValidFromDate());
        assertEquals(validToDate, alertResource.getValidToDate());
    }

    @Test
    public void test_alertResourceShouldReturnCorrectAttributeValuesAfterSetters() throws Exception {
        final Long id = 9999L;
        final String message = "Sample message";
        final AlertType type = MAINTENANCE;
        final LocalDateTime validFromDate = LocalDateTime.parse("2016-05-06T21:00:00.00");
        final LocalDateTime validToDate = LocalDateTime.parse("2016-05-06T21:05:00.00");

        final AlertResource alertResource = new AlertResource();

        alertResource.setId(id);
        alertResource.setMessage(message);
        alertResource.setType(type);
        alertResource.setValidFromDate(validFromDate);
        alertResource.setValidToDate(validToDate);

        assertEquals(id, alertResource.getId());
        assertEquals(message, alertResource.getMessage());
        assertEquals(type, alertResource.getType());
        assertEquals(validFromDate, alertResource.getValidFromDate());
        assertEquals(validToDate, alertResource.getValidToDate());
    }

}
