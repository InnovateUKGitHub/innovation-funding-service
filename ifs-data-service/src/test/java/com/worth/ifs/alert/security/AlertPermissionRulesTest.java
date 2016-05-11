package com.worth.ifs.alert.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
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
    public void anyoneCanViewAlerts() throws Exception {
        assertTrue(rules.anyoneCanViewAlerts(alertResource, getGeneralUser()));
    }

    @Test
    public void test_competitionsAdminCanCreateAlerts() throws Exception {
        assertTrue(rules.competitionsAdminCanCreateAlerts(alertResource, compAdminUser()));
    }

    @Test
    public void test_competitionsAdminCanDeleteAlerts() throws Exception {
        assertTrue(rules.competitionsAdminCanDeleteAlerts(alertResource, compAdminUser()));
    }

    private UserResource getGeneralUser() {
        return newUserResource().build();
    }
}