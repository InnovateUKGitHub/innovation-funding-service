package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ApplicationTeamAddOrganisationControllerSecurityTest extends BaseApplicationControllerSecurityTest<ApplicationTeamAddOrganisationController> {

    @Override
    protected Class<? extends ApplicationTeamAddOrganisationController> getClassUnderTest() {
        return ApplicationTeamAddOrganisationController.class;
    }

    @Test
    public void testGetAddOrganisation() {
        assertSecured(() -> classUnderTest.getAddOrganisation(null, 123L, null, null),
                permissionRules -> permissionRules.isLeadApplicant(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testSubmitAddOrganisation() {
        assertSecured(() -> classUnderTest.submitAddOrganisation(null, 123L, null, null, null, null),
                permissionRules -> permissionRules.isLeadApplicant(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testAddApplicant() {
        assertSecured(() -> classUnderTest.addApplicant(null, 123L, null, null),
                permissionRules -> permissionRules.isLeadApplicant(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testRemoveApplicant() {
        assertSecured(() -> classUnderTest.removeApplicant(null, 123L, null, null, null),
                permissionRules -> permissionRules.isLeadApplicant(eq(123L), isA(UserResource.class)));
    }

/*    @Override
    protected Consumer<ApplicationPermissionRules> getVerification() {
        return permissionRules -> permissionRules.isLeadApplicant(eq(123L), isA(UserResource.class));
    }*/
}
