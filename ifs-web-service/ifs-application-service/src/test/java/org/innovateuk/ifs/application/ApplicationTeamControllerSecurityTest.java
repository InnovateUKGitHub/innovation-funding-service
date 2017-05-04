package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ApplicationTeamControllerSecurityTest extends BaseApplicationControllerSecurityTest<ApplicationTeamController> {

    @Override
    protected Class<? extends ApplicationTeamController> getClassUnderTest() {
        return ApplicationTeamController.class;
    }

    @Test
    public void testGetApplicationTeam() {
        assertSecured(() -> classUnderTest.getApplicationTeam(null, 123L, null),
                (ApplicationPermissionRules permissionRules) -> permissionRules.viewApplicationTeamPage(eq(123L), isA(UserResource.class)),
                ApplicationPermissionRules.class);
    }

    @Test
    public void testBeginApplication() {
        assertSecured(() -> classUnderTest.beginApplication(123L, null),
                (ApplicationPermissionRules permissionRules) -> permissionRules.beginApplication(eq(123L), isA(UserResource.class)),
                ApplicationPermissionRules.class);
    }
}
