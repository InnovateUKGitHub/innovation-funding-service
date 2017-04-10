package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileApprovalViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSpendProfileApprovalControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileApprovalController> {

    @Test
    public void viewSpendProfileApprovalSuccess() throws Exception {
        Long projectId = 123L;
        Long applicationId = 20L;
        Long competitionId = 2319L;
        Long userId = 239L;

        UserResource user = newUserResource().withId(userId).build();
        CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().withId(competitionId).build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).withLeadTechnologist(userId).build();
        ApplicationResource application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();
        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(competitionService.getById(competitionId)).thenReturn(competition);
        when(userService.findById(userId)).thenReturn(user);
        when(projectFinanceService.getSpendProfileStatusByProjectId(projectId)).thenReturn(ApprovalType.APPROVED);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.emptyList());

        ProjectSpendProfileApprovalViewModel expectedProjectSpendProfileApprovalViewModel =
                new ProjectSpendProfileApprovalViewModel(competitionSummary, user.getName(), ApprovalType.APPROVED, Collections.emptyList());

        mockMvc.perform(get("/project/{projectId}/spend-profile/approval", project.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileApprovalViewModel))
                .andExpect(view().name("project/finance/spend-profile/approval"));
    }


    @Override
    protected ProjectSpendProfileApprovalController supplyControllerUnderTest() {
        return new ProjectSpendProfileApprovalController();
    }
}
