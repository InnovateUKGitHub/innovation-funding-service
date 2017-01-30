package org.innovateuk.ifs.project.financechecks;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksController;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectFinanceChecksControllerTest extends BaseControllerMockMVCTest<ProjectFinanceChecksController> {

    @Test
    public void testViewFinanceChecksLandingPage() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);

        MvcResult result = mockMvc.perform(get("/project/123/partner-organisation/234/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertFalse(model.isApproved());

    }

    @Test
    public void testViewFinanceChecksLandingPageApproved() throws Exception {

        long projectId = 123L;
        long organisationId = 234L;
        String projectName = "Name";

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        ProjectResource project = newProjectResource().withId(projectId).withName(projectName).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);

        MvcResult result = mockMvc.perform(get("/project/123/partner-organisation/234/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), projectName);
        assertTrue(model.isApproved());
    }

        @Override
    protected ProjectFinanceChecksController supplyControllerUnderTest() {
        return new ProjectFinanceChecksController();
    }
}
