package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.application.overview.controller.BaseApplicationControllerSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationCompositeId;
import org.innovateuk.ifs.application.team.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.team.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class ApplicationTeamControllerSecurityTest extends BaseApplicationControllerSecurityTest<ApplicationTeamController> {

    ApplicationLookupStrategy applicationLookupStrategies;

    @Before
    public void lookupPermissionRules(){
        applicationLookupStrategies = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Override
    protected Class<? extends ApplicationTeamController> getClassUnderTest() {
        return ApplicationTeamController.class;
    }

    @Test
    public void testGetApplicationTeam() {
        when(applicationLookupStrategies.getApplicationCompositeId(123L)).thenReturn(ApplicationCompositeId.id(123L));
        assertSecured(() -> classUnderTest.getApplicationTeam(null, 123L, null),
                (ApplicationPermissionRules permissionRules) -> permissionRules.viewApplicationTeamPage(eq(ApplicationCompositeId.id(123L)), isA(UserResource.class)),
                ApplicationPermissionRules.class);
    }
}
