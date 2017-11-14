package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.overview.controller.BaseApplicationControllerSecurityTest;
import org.innovateuk.ifs.application.team.security.ApplicationPermissionRules;
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
}
