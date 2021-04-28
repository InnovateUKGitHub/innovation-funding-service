package org.innovateuk.ifs.security;

import org.innovateuk.ifs.RootPermissionRulesTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlertPermissionRulesTest extends RootPermissionRulesTest<AlertPermissionRules> {

    private AlertResource alertResource;

    @Before
    public void setUp() {
        alertResource = AlertResourceBuilder.newAlertResource()
                .build();
    }

    @Override
    protected AlertPermissionRules supplyPermissionRulesUnderTest() {
        return new AlertPermissionRules();
    }

    @Test
    public void systemMaintenanceUserCanCreateAlerts() {
        assertTrue(rules.systemMaintenanceUserCanCreateAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void systemMaintenanceUserCanCreateAlerts_anonymous() {
        assertFalse(rules.systemMaintenanceUserCanCreateAlerts(alertResource, anonymousUser()));
    }

    @Test
    public void systemMaintenanceUserCanDeleteAlerts() {
        assertTrue(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void systemMaintenanceUserCanDeleteAlerts_anonymous() {
        assertFalse(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, anonymousUser()));
    }
}
