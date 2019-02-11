package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class ProjectDetailsAddressControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectDetailsAddressController> {

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
    protected Class<? extends ProjectDetailsAddressController> getClassUnderTest() {
        return ProjectDetailsAddressController.class;
    }

    @Test
    public void testViewAddress() {
        assertSecured(() -> classUnderTest.viewAddress(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUpdateAddress() {
        assertSecured(() -> classUnderTest.updateAddress(projectCompositeId.id(), null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testAddressFormAction() {
        assertSecured(() -> classUnderTest.addressFormAction(projectCompositeId.id(), null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }
}
