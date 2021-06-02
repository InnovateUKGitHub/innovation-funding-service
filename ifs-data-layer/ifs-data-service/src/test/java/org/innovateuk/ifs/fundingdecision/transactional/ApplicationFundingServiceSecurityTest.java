package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.EnumSet;
import java.util.HashMap;

import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.fail;

public class ApplicationFundingServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFundingService> {

    private static final EnumSet<Role> NON_COMP_ADMIN_ROLES = complementOf(of(COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR, SUPER_ADMIN_USER, SYSTEM_MAINTAINER));

    @Test
    public void notifyLeadApplicantAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRoleGlobal(Role.COMP_ADMIN).build());
        classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
    }

    @Test
    public void notifyLeadApplicantDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
            fail("Should not have been able to notify lead applicants of funding decision without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void notifyLeadApplicantDeniedIfNoGlobalRolesAtAll() {

        try {
            classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
            fail("Should not have been able to notify lead applicants of funding decision without the global comp " +
                    "admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void notifyLeadApplicantDeniedIfNotCorrectGlobalRoles() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRoleGlobal(role).build());
            try {
                classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
                fail("Should not have been able to notify lead applicants of funding decision without the global Comp" +
                        " Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void saveFundingDecisionDataAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRoleGlobal(Role.COMP_ADMIN).build());
        classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
    }

    @Test
    public void saveFundingDecisionDataDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
            fail("Should not have been able to save funding decision data without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void saveFundingDecisionDataDeniedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
            fail("Should not have been able to save funding decision data without the global comp admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void saveFundingDecisionDataDeniedIfNotCorrectGlobalRoles() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRoleGlobal(role).build());
            try {
                classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
                fail("Should not have been able to save funding decision data without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Override
    protected Class<? extends ApplicationFundingService> getClassUnderTest() {
        return ApplicationFundingServiceImpl.class;
    }
}