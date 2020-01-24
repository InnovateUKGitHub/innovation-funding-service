package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class ProjectDetailsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectDetailsController> {

    private ProjectCompositeId projectCompositeId;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        ProjectLookupStrategy projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123L);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends ProjectDetailsController> getClassUnderTest() {
        return ProjectDetailsController.class;
    }

    @Test
    public void viewProjectDetails() {
        assertSecured(() -> classUnderTest.viewProjectDetails(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }
}