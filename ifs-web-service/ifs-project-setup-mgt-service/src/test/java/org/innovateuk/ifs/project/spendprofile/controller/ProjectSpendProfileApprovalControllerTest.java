package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileApprovalViewModel;
import org.innovateuk.ifs.spendprofile.SpendProfileService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectSpendProfileApprovalControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileApprovalController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private SpendProfileService spendProfileService;

    @Test
    public void viewSpendProfileApprovalSuccess() throws Exception {
        long projectId = 123L;
        long applicationId = 20L;
        long competitionId = 2319L;
        long userId = 239L;

        UserResource user = newUserResource().withId(userId).build();
        CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().withId(competitionId).build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).withLeadTechnologist(userId).build();
        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).withProjectState(SETUP).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(user));
        when(spendProfileService.getSpendProfileStatusByProjectId(projectId)).thenReturn(ApprovalType.APPROVED);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(Collections.emptyList());

        ProjectSpendProfileApprovalViewModel expectedProjectSpendProfileApprovalViewModel =
                new ProjectSpendProfileApprovalViewModel(competitionSummary, user.getName(), ApprovalType.APPROVED, Collections.emptyList(), project);

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
