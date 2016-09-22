package com.worth.ifs.project;

import com.worth.ifs.security.BaseControllerSecurityTest;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ProjectDetailsControllerSecurityTest extends BaseControllerSecurityTest<ProjectDetailsController> {

    @Override
    protected Class<? extends ProjectDetailsController> getClassUnderTest() {
        return ProjectDetailsController.class;
    }

    private ProjectSetupSectionsPermissionRules permissionRules;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(ProjectSetupSectionsPermissionRules.class);
    }

    @Test
    public void testViewProjectDetails() {
        assertSecured(() -> classUnderTest.viewProjectDetails(123L, null, null));
    }

    @Test
    public void testProjectDetailConfirmSubmit() {
        assertSecured(() -> classUnderTest.projectDetailConfirmSubmit(123L, null, null));
    }

    @Test
    public void testViewFinanceContact() {
        assertSecured(() -> classUnderTest.viewFinanceContact(123L, null, null, null));
    }

    @Test
    public void testUpdateFinanceContact() {
        assertSecured(() -> classUnderTest.updateFinanceContact(123L, null, null, null, null, null));
    }

    @Test
    public void testViewProjectManager() {
        assertSecured(() -> classUnderTest.viewProjectManager(123L, null, null));
    }

    @Test
    public void testUpdateProjectManager() {
        assertSecured(() -> classUnderTest.updateProjectManager(123L, null, null, null, null, null));
    }

    @Test
    public void testViewStartDate() {
        assertSecured(() -> classUnderTest.viewStartDate(123L, null, null, null));
    }

    @Test
    public void testUpdateStartDate() {
        assertSecured(() -> classUnderTest.updateStartDate(123L, null, null, null, null, null));
    }

    @Test
    public void testViewAddress() {
        assertSecured(() -> classUnderTest.viewAddress(123L, null, null));
    }

    @Test
    public void testSearchAddress() {
        assertSecured(() -> classUnderTest.searchAddress(123L, null, null));
    }

    @Test
    public void testUpdateAddress() {
        assertSecured(() -> classUnderTest.updateAddress(123L, null, null, null, null));
    }

    @Test
    public void testSelectAddress() {
        assertSecured(() -> classUnderTest.selectAddress(123L, null, null));
    }

    @Test
    public void testManualAddress() {
        assertSecured(() -> classUnderTest.manualAddress(123L, null, null));
    }

    @Test
    public void testSubmitProjectDetails() {
        assertSecured(() -> classUnderTest.submitProjectDetails(123L));
    }

    private void assertSecured(Runnable invokeControllerFn) {
        assertAccessDenied(
                invokeControllerFn::run,
                () -> verify(permissionRules, times(1)).partnerCanAccessProjectDetailsSection(eq(123L), isA(UserResource.class))
        );
    }
}
