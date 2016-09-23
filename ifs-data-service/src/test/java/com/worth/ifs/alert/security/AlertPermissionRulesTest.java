package com.worth.ifs.alert.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.security.CustomPermissionEvaluator;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static com.worth.ifs.user.resource.UserRoleType.SYSTEM_MAINTAINER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlertPermissionRulesTest extends BasePermissionRulesTest<AlertPermissionRules> {

    private AlertResource alertResource;
    private UserResource anonymousUser;

    @Before
    public void setUp() throws Exception {
        alertResource = newAlertResource()
                .build();

        anonymousUser = (UserResource) ReflectionTestUtils.getField(new CustomPermissionEvaluator(), "ANONYMOUS_USER");
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
        assertFalse(rules.systemMaintenanceUserCanCreateAlerts(alertResource, anonymousUser));
    }

    @Test
    public void test_systemMaintenanceUserCanDeleteAlerts() throws Exception {
        assertTrue(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, systemMaintenanceUser()));
    }

    @Test
    public void test_systemMaintenanceUserCanDeleteAlerts_anonymous() throws Exception {
        assertFalse(rules.systemMaintenanceUserCanDeleteAlerts(alertResource, anonymousUser));
    }

    private UserResource systemMaintenanceUser() {
        return getUserWithRole(SYSTEM_MAINTAINER);
    }
}