package org.innovateuk.ifs.alert.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static org.innovateuk.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.innovateuk.ifs.commons.security.SecuritySetter.basicSecurityUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

public class AlertControllerIntegrationTest extends BaseControllerIntegrationTest<AlertController> {

    private UserResource systemMaintenanceUser;

    @Before
    public void setUp() throws Exception {
        systemMaintenanceUser = newUserResource().withRolesGlobal(singletonList(Role.SYSTEM_MAINTAINER)).build();
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(AlertController controller) {
        this.controller = controller;
    }

    @Test
    public void test_findAllVisible() throws Exception {
        // save new alerts with date ranges that should make them visible now
        ZonedDateTime now = now();
        ZonedDateTime oneSecondAgo = now.minusSeconds(1);
        ZonedDateTime oneDayAgo = now.minusDays(1);
        ZonedDateTime oneHourAhead = now.plusHours(1);
        ZonedDateTime oneDayAhead = now.plusDays(1);

        setLoggedInUser(systemMaintenanceUser);
        AlertResource created1 = controller.create(newAlertResource()
                .withValidFromDate(oneDayAgo)
                .withValidToDate(oneDayAhead)
                .build()).getSuccess();

        AlertResource created2 = controller.create(newAlertResource()
                .withValidFromDate(oneSecondAgo)
                .withValidToDate(oneHourAhead)
                .build()).getSuccess();

        setLoggedInUser(basicSecurityUser);
        List<AlertResource> found = controller.findAllVisible().getSuccess();

        assertEquals(2, found.size());
        assertEquals(created1, found.get(0));
        assertEquals(created2, found.get(1));
    }

    @Test
    public void test_findAllVisibleByType() throws Exception {
        // save new alerts with date ranges that should make them visible now
        ZonedDateTime now = now();
        ZonedDateTime oneSecondAgo = now.minusSeconds(1);
        ZonedDateTime oneDayAgo = now.minusDays(1);
        ZonedDateTime oneHourAhead = now.plusHours(1);
        ZonedDateTime oneDayAhead = now.plusDays(1);

        setLoggedInUser(systemMaintenanceUser);
        AlertResource created1 = controller.create(newAlertResource()
                .withValidFromDate(oneDayAgo)
                .withValidToDate(oneDayAhead)
                .build()).getSuccess();

        AlertResource created2 = controller.create(newAlertResource()
                .withValidFromDate(oneSecondAgo)
                .withValidToDate(oneHourAhead)
                .build()).getSuccess();

        setLoggedInUser(basicSecurityUser);
        List<AlertResource> found = controller.findAllVisibleByType(MAINTENANCE).getSuccess();

        assertEquals(2, found.size());
        assertEquals(created1, found.get(0));
        assertEquals(created2, found.get(1));
    }

    @Test
    public void test_findById() throws Exception {
        setLoggedInUser(basicSecurityUser);
        AlertResource found = controller.findById(1L).getSuccess();

        assertEquals(Long.valueOf(1L), found.getId());
        assertEquals("Sample message", found.getMessage());
        assertEquals(MAINTENANCE, found.getType());
        assertEquals(LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), found.getValidFromDate());
        assertEquals(LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()), found.getValidToDate());
    }

    @Test
    public void test_create() throws Exception {
        setLoggedInUser(systemMaintenanceUser);

        AlertResource alertResource = newAlertResource()
                .build();

        AlertResource created = controller.create(alertResource).getSuccess();
        assertNotNull(created.getId());
        assertEquals("Sample message", created.getMessage());
        assertEquals(MAINTENANCE, created.getType());
        assertEquals(LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.of("UTC")), created.getValidFromDate());
        assertEquals(LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.of("UTC")), created.getValidToDate());

        // check it can also be found
        assertNotNull(controller.findById(created.getId()));
    }

    @Test
    public void test_delete() throws Exception {
        setLoggedInUser(systemMaintenanceUser);

        // save a new alert
        AlertResource alertResource = newAlertResource()
                .withId()
                .build();

        AlertResource created = controller.create(alertResource).getSuccess();

        // check that it can be found
        assertNotNull(created.getId());
        alertResource.setId(created.getId());
        assertEquals(alertResource, controller.findById(created.getId()).getSuccess());

        // now delete it
        controller.delete(created.getId()).getSuccess();

        // make sure it can't be found
        assertTrue(controller.findById(created.getId()).isFailure());
    }

    @Test
    public void test_deleteAllByType() throws Exception {
        setLoggedInUser(systemMaintenanceUser);

        controller.deleteAllByType(AlertType.MAINTENANCE);

        // make sure they can't be found
        assertTrue(controller.findAllVisibleByType(MAINTENANCE).getSuccess().isEmpty());
    }
}
