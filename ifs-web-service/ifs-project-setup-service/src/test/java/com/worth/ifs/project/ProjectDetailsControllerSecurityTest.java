package com.worth.ifs.project;

import org.junit.Test;

public class ProjectDetailsControllerSecurityTest extends BaseControllerSecurityTest<ProjectDetailsController, ProjectDetailsControllerSecurityAspect, ProjectDetailsControllerSecurityAdvisor> {

    @Test
    public void testViewProjectDetails() {
        assertSecured(controller -> controller.viewProjectDetails(123L, null, null));
    }

    @Test
    public void testProjectDetailConfirmSubmit() {
        assertSecured(controller -> controller.projectDetailConfirmSubmit(123L, null, null));
    }

    @Test
    public void testViewFinanceContact() {
        assertSecured(controller -> controller.viewFinanceContact(123L, null, null, null));
    }

    @Test
    public void testUpdateFinanceContact() {
        assertSecured(controller -> controller.updateFinanceContact(123L, null, null, null, null, null));
    }

    @Test
    public void testViewProjectManager() {
        assertSecured(controller -> controller.viewProjectManager(123L, null, null));
    }

    @Test
    public void testUpdateProjectManager() {
        assertSecured(controller -> controller.updateProjectManager(123L, null, null, null, null, null));
    }

    @Test
    public void testViewStartDate() {
        assertSecured(controller -> controller.viewStartDate(123L, null, null, null));
    }

    @Test
    public void testUpdateStartDate() {
        assertSecured(controller -> controller.updateStartDate(123L, null, null, null, null, null));
    }

    @Test
    public void testViewAddress() {
        assertSecured(controller -> controller.viewAddress(123L, null, null));
    }

    @Test
    public void testSearchAddress() {
        assertSecured(controller -> controller.searchAddress(123L, null, null));
    }

    @Test
    public void testUpdateAddress() {
        assertSecured(controller -> controller.updateAddress(123L, null, null, null, null));
    }

    @Test
    public void testSelectAddress() {
        assertSecured(controller -> controller.selectAddress(123L, null, null));
    }

    @Test
    public void testManualAddress() {
        assertSecured(controller -> controller.manualAddress(123L, null, null));
    }

    @Test
    public void testSubmitProjectDetails() {
        assertSecured(controller -> controller.submitProjectDetails(123L));
    }

    @Override
    protected Class<ProjectDetailsController> getControllerType() {
        return ProjectDetailsController.class;
    }

    @Override
    protected Class<ProjectDetailsControllerSecurityAspect> getAspectType() {
        return ProjectDetailsControllerSecurityAspect.class;
    }

    @Override
    protected Class<ProjectDetailsControllerSecurityAdvisor> getAdvisorType() {
        return ProjectDetailsControllerSecurityAdvisor.class;
    }

    @Override
    protected boolean getExpectedSecurityCheck(ProjectDetailsControllerSecurityAdvisor advisor) {
        return advisor.canAccessProjectDetailsSection(123L);
    }

    @Override
    protected String getExpectedForbiddenMessage() {
        return "Unable to access the Project Details section at this time";
    }
}
