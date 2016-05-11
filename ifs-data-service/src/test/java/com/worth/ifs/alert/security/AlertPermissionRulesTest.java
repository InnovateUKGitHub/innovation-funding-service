package com.worth.ifs.alert.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
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
    public void test_competitionsAdminCanCreateAlerts() throws Exception {
        assertTrue(rules.competitionsAdminCanCreateAlerts(alertResource, compAdminUser()));
    }

    @Test
    public void test_competitionsAdminCanCreateAlerts_anonymous() throws Exception {
        assertFalse(rules.competitionsAdminCanCreateAlerts(alertResource, anonymousUser));
    }

    @Test
    public void test_competitionsAdminCanDeleteAlerts() throws Exception {
        assertTrue(rules.competitionsAdminCanDeleteAlerts(alertResource, compAdminUser()));
    }

    @Test
    public void test_competitionsAdminCanDeleteAlerts_anonymous() throws Exception {
        assertFalse(rules.competitionsAdminCanDeleteAlerts(alertResource, anonymousUser));
    }
}