package org.innovateuk.ifs.alert.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_MAINTAINER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlertPermissionRulesTest extends BasePermissionRulesTest<AlertPermissionRules> {

    private AlertResource alertResource;

    @Before
    public void setUp() throws Exception {
        alertResource = newAlertResource()
                .build();
    }

    @Override
    protected AlertPermissionRules supplyPermissionRulesUnderTest() {
        return new AlertPermissionRules();
    }

    @Test
    public void test_systemMaintenanceUserCanCreateAlerts() throws Exception {
        assertTrue(rules.systemMaintenanceUserCanCreateAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void test_systemMaintenanceUserCanCreateAlerts_anonymous() throws Exception {
        assertFalse(rules.systemMaintenanceUserCanCreateAlerts(alertResource, anonymousUser()));
    }

    @Test
    public void test_systemMaintenanceUserCanDeleteAlerts() throws Exception {
        assertTrue(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void test_systemMaintenanceUserCanDeleteAlerts_anonymous() throws Exception {
        assertFalse(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, anonymousUser()));
    }

    private UserResource systemMaintenanceUser() {
        return getUserWithRole(SYSTEM_MAINTAINER);
    }
}
