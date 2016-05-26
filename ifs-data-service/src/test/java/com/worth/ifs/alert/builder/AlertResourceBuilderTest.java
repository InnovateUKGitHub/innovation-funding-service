package com.worth.ifs.alert.builder;

import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.resource.AlertResource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AlertResourceBuilderTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_buildOne() {
        final Long expectedId = 9999L;
        final String expectedMessage = "Sample message";
        final AlertType expectedType = AlertType.MAINTENANCE;
        final LocalDateTime expectedValidFromDate = LocalDateTime.parse("2016-05-06T21:00:00.00");
        final LocalDateTime expectedValidToDate = LocalDateTime.parse("2016-05-06T21:05:00.00");
        final AlertResource alertResource = AlertResourceBuilder
                .newAlertResource()
                .withId(expectedId)
                .withMessage(expectedMessage)
                .withType(expectedType)
                .withValidFromDate(expectedValidFromDate)
                .withValidToDate(expectedValidToDate)
                .build();

        assertEquals(Long.valueOf(expectedId), alertResource.getId());
        assertEquals(expectedMessage, alertResource.getMessage());
        assertEquals(expectedType, alertResource.getType());
        assertEquals(expectedValidFromDate, alertResource.getValidFromDate());
        assertEquals(expectedValidToDate, alertResource.getValidToDate());
    }

    @Test
    public void test_buildMany() {
        final Long[] expectedIds = { 8888L, 9999L };
        final String[] expectedMessages = { "Sample message 1", "Sample message 2" };
        final AlertType[] expectedTypes = { AlertType.MAINTENANCE, AlertType.MAINTENANCE };
        final LocalDateTime[] expectedValidFromDates = { LocalDateTime.parse("2016-05-06T21:00:00.00"), LocalDateTime.parse("2016-05-06T22:00:00.00") };
        final LocalDateTime[] expectedValidToDates = { LocalDateTime.parse("2016-05-06T21:05:00.00"), LocalDateTime.parse("2016-05-06T22:05:00.00") };
        final List<AlertResource> alertResources = AlertResourceBuilder
                .newAlertResource()
                .withId(expectedIds)
                .withMessage(expectedMessages)
                .withType(expectedTypes)
                .withValidFromDate(expectedValidFromDates)
                .withValidToDate(expectedValidToDates)
                .build(2);

        final AlertResource first = alertResources.get(0);
        assertEquals(Long.valueOf(expectedIds[0]), first.getId());
        assertEquals(expectedMessages[0], first.getMessage());
        assertEquals(expectedTypes[0], first.getType());
        assertEquals(expectedValidFromDates[0], first.getValidFromDate());
        assertEquals(expectedValidToDates[0], first.getValidToDate());

        final AlertResource second = alertResources.get(1);
        assertEquals(Long.valueOf(expectedIds[1]), second.getId());
        assertEquals(expectedMessages[1], second.getMessage());
        assertEquals(expectedTypes[1], second.getType());
        assertEquals(expectedValidFromDates[1], second.getValidFromDate());
        assertEquals(expectedValidToDates[1], second.getValidToDate());
    }

}