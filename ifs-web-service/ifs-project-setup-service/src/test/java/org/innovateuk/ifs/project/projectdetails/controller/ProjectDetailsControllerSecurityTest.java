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

public class ProjectDetailsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectDetailsController> {

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
    protected Class<? extends ProjectDetailsController> getClassUnderTest() {
        return ProjectDetailsController.class;
    }

    @Test
    public void testViewProjectDetails() {
        assertSecured(() -> classUnderTest.viewProjectDetails(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testViewFinanceContact() {
        assertSecured(() -> classUnderTest.viewFinanceContact(projectCompositeId.id(), null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessFinanceContactPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUpdateFinanceContact() {

        assertSecured(() -> classUnderTest.updateFinanceContact(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessFinanceContactPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testInviteFinanceContact() {

        assertSecured(() -> classUnderTest.inviteFinanceContact(null, projectCompositeId.id(), 123L, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessFinanceContactPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testInviteProjectManager() {

        assertSecured(() -> classUnderTest.inviteProjectManager(null, projectCompositeId.id(), null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectManagerPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testViewProjectManager() {
        assertSecured(() -> classUnderTest.viewProjectManager(projectCompositeId.id(), null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectManagerPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUpdateProjectManager() {
        assertSecured(() -> classUnderTest.updateProjectManager(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectManagerPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testViewStartDate() {
        assertSecured(() -> classUnderTest.viewStartDate(projectCompositeId.id(), null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectStartDatePage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testUpdateStartDate() {
        assertSecured(() -> classUnderTest.updateStartDate(projectCompositeId.id(), null, null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectStartDatePage(eq(projectCompositeId), isA(UserResource.class)));
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
    public void testSearchAddress() {
        assertSecured(() -> classUnderTest.searchAddress(projectCompositeId.id(), null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testSelectAddress() {
        assertSecured(() -> classUnderTest.selectAddress(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testManualAddress() {
        assertSecured(() -> classUnderTest.manualAddress(projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(projectCompositeId), isA(UserResource.class)));
    }
}
