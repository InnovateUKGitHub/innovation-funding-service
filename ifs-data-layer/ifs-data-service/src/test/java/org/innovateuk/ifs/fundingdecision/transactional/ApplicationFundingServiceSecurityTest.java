package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.EnumSet;
import java.util.HashMap;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.fail;

public class ApplicationFundingServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFundingService> {

    private static final EnumSet<Role> NON_COMP_ADMIN_ROLES = complementOf(of(COMP_ADMIN, PROJECT_FINANCE));

    @Test
    public void testNotifyLeadApplicantAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
    }

    @Test
    public void testNotifyLeadApplicantDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
            fail("Should not have been able to notify lead applicants of funding decision without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testNotifyLeadApplicantDeniedIfNoGlobalRolesAtAll() {

        try {
            classUnderTest.notifyApplicantsOfFundingDecisions(new FundingNotificationResource());
            fail("Should not have been able to notify lead applicants of funding decision without the global comp " +
                    "admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testNotifyLeadApplicantDeniedIfNotCorrectGlobalRoles() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(role)).build());
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
    public void testSaveFundingDecisionDataAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
    }

    @Test
    public void testSaveFundingDecisionDataDeniedIfNotLoggedIn() {

        setLoggedInUser(null);
        try {
            classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
            fail("Should not have been able to save funding decision data without first logging in");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testSaveFundingDecisionDataDeniedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.saveFundingDecisionData(123L, new HashMap<>());
            fail("Should not have been able to save funding decision data without the global comp admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testSaveFundingDecisionDataDeniedIfNotCorrectGlobalRoles() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(role)).build());
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
