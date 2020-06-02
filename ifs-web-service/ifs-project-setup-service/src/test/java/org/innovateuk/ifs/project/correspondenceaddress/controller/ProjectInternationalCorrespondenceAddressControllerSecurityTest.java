package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class ProjectInternationalCorrespondenceAddressControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectInternationalCorrespondenceAddressController> {

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
    protected Class<? extends ProjectInternationalCorrespondenceAddressController> getClassUnderTest() {
        return ProjectInternationalCorrespondenceAddressController.class;
    }

    @Test
    public void viewAddress() {
        assertSecured(() -> classUnderTest.viewAddress(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void updateAddress() {
        assertSecured(() -> classUnderTest.updateAddress(projectCompositeId.id(), null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }
}