package org.innovateuk.ifs.security;

import org.innovateuk.ifs.RootPermissionRulesTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.SYSTEM_MAINTAINER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlertPermissionRulesTest extends RootPermissionRulesTest<AlertPermissionRules> {

    private AlertResource alertResource;

    @Before
    public void setUp() throws Exception {
        alertResource = AlertResourceBuilder.newAlertResource()
                .build();
    }

    @Override
    protected AlertPermissionRules supplyPermissionRulesUnderTest() {
        return new AlertPermissionRules();
    }

    @Test
    public void systemMaintenanceUserCanCreateAlerts() throws Exception {
        assertTrue(rules.systemMaintenanceUserCanCreateAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void systemMaintenanceUserCanCreateAlerts_anonymous() throws Exception {
        assertFalse(rules.systemMaintenanceUserCanCreateAlerts(alertResource, anonymousUser()));
    }

    @Test
    public void systemMaintenanceUserCanDeleteAlerts() throws Exception {
        assertTrue(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void systemMaintenanceUserCanDeleteAlerts_anonymous() throws Exception {
        assertFalse(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, anonymousUser()));
    }

    private UserResource systemMaintenanceUser() {
        return getUserWithRole(SYSTEM_MAINTAINER);
    }
}
