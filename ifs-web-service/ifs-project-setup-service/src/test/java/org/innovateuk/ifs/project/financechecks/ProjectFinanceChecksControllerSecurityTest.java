package org.innovateuk.ifs.project.financechecks;


import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksController;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class ProjectFinanceChecksControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectFinanceChecksController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;


    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends ProjectFinanceChecksController> getClassUnderTest() {
        return ProjectFinanceChecksController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.viewFinanceChecks(null, projectCompositeId.id(), null),
                permissionRules -> permissionRules.partnerCanAccessFinanceChecksSection(eq(projectCompositeId), isA(UserResource.class)));
    }
}