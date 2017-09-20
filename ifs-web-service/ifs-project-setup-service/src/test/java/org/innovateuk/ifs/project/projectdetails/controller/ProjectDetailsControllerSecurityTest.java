package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectDetailsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectDetailsController> {

    @Override
    protected Class<? extends ProjectDetailsController> getClassUnderTest() {
        return ProjectDetailsController.class;
    }

    @Test
    public void testViewProjectDetails() {
        assertSecured(() -> classUnderTest.viewProjectDetails(123L, null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class)));
    }

/*    @Test
    public void testProjectDetailConfirmSubmit() {
        assertSecured(() -> classUnderTest.projectDetailConfirmSubmit(123L, null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class)));
    }*/

    @Test
    public void testViewFinanceContact() {
        assertSecured(() -> classUnderTest.viewFinanceContact(123L, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUpdateFinanceContact() {

        assertSecured(() -> classUnderTest.updateFinanceContact(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testViewProjectManager() {
        assertSecured(() -> classUnderTest.viewProjectManager(123L, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectManagerPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUpdateProjectManager() {
        assertSecured(() -> classUnderTest.updateProjectManager(123L, null, null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectManagerPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testViewStartDate() {
        assertSecured(() -> classUnderTest.viewStartDate(123L, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectStartDatePage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUpdateStartDate() {
        assertSecured(() -> classUnderTest.updateStartDate(123L, null, null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectStartDatePage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testViewAddress() {
        assertSecured(() -> classUnderTest.viewAddress(123L, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testSearchAddress() {
        assertSecured(() -> classUnderTest.searchAddress(123L, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testUpdateAddress() {
        assertSecured(() -> classUnderTest.updateAddress(123L, null, null, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testSelectAddress() {
        assertSecured(() -> classUnderTest.selectAddress(123L, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(123L), isA(UserResource.class)));
    }

    @Test
    public void testManualAddress() {
        assertSecured(() -> classUnderTest.manualAddress(123L, null, null),
                permissionRules -> permissionRules.leadCanAccessProjectAddressPage(eq(123L), isA(UserResource.class)));
    }

/*    @Test
    public void testSubmitProjectDetails() {
        assertSecured(() -> classUnderTest.submitProjectDetails(123L),
                permissionRules -> permissionRules.partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class)));
    }*/
}
