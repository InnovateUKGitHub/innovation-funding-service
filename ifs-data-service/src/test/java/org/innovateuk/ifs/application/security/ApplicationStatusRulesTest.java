package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.ApplicationStatus;
import org.innovateuk.ifs.application.resource.ApplicationStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ApplicationStatusRulesTest extends BasePermissionRulesTest<ApplicationStatusRules> {

    @Override
    protected ApplicationStatusRules supplyPermissionRulesUnderTest() {
        return new ApplicationStatusRules();
    }

    @Test
    public void testUserCanReadApplicationStatus() {
        boolean result = rules.userCanReadApplicationStatus(new ApplicationStatus(), new UserResource());
        assertTrue(result);
    }

    @Test
    public void testUserCanReadApplicationStatusResource() {
        boolean result =  rules.userCanReadApplicationStatusResource(new ApplicationStatusResource(), new UserResource());
        assertTrue(result);
    }

}
