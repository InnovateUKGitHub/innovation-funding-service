package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.fail;

public class ApplicationSummarisationServiceSecurityTest extends
        BaseServiceSecurityTest<ApplicationSummarisationService> {

    @Test
    public void testTotalProjectCostAllowedIfGlobalCompAdminRole() {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.getTotalProjectCost(null);
    }

    @Test
    public void testTotalProjectCostDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.getTotalProjectCost(null);
            fail("Should not have been able to get total project cost without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testTotalCostDeniedIfNoGlobalRolesAtAll() {

        try {
            classUnderTest.getTotalProjectCost(null);
            fail("Should not have been able to get total project cost without the global comp admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testFundingSoughtAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.getFundingSought(null);
    }

    @Test
    public void testFundingSoughtDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.getFundingSought(null);
            fail("Should not have been able to get funding sought without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testFundingSoughtDeniedIfNoGlobalRolesAtAll() {

        try {
            classUnderTest.getFundingSought(null);
            fail("Should not have been able to get funding sought without the global comp admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Override
    protected Class<? extends ApplicationSummarisationService> getClassUnderTest() {
        return ApplicationSummarisationServiceImpl.class;
    }
}
