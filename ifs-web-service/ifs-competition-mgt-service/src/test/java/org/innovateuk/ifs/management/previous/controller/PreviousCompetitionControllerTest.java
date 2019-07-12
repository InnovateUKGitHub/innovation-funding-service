package org.innovateuk.ifs.management.previous.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.previous.controller.PreviousCompetitionController;
import org.innovateuk.ifs.management.competition.previous.viewmodel.PreviousCompetitionViewModel;
import org.innovateuk.ifs.management.funding.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.PreviousApplicationResourceBuilder.newPreviousApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class PreviousCompetitionControllerTest extends BaseControllerMockMVCTest<PreviousCompetitionController> {

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Mock
    private ProjectRestService projectRestService;

    @Test
    public void viewPreviousCompetition() throws Exception {
        long competitionId = 1L;
        ZonedDateTime close = ZonedDateTime.now();

        setLoggedInUser(newUserResource().withRoleGlobal(Role.IFS_ADMINISTRATOR).build());
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("competition")
                .withCompetitionTypeName("type")
                .withEndDate(close)
                .withInnovationSectorName("sector")
                .build();

        List<PreviousApplicationResource> previousApplications = newPreviousApplicationResource().build(1);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationSummaryRestService.getPreviousApplications(competitionId)).thenReturn(restSuccess(previousApplications));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/previous", competitionId))
                .andExpect(view().name("competition/previous"))
                .andReturn();

        PreviousCompetitionViewModel viewModel = (PreviousCompetitionViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals("competition", viewModel.getCompetitionName());
        assertEquals("type", viewModel.getCompetitionType());
        assertEquals(close, viewModel.getApplicationDeadline());
        assertEquals("Innovate UK", viewModel.getFundingBody());
        assertEquals("sector", viewModel.getInnovationSector());
        assertEquals(true, viewModel.isIfsAdmin());

        String originQuery = (String) result.getModelAndView().getModel().get("originQuery");

        assertEquals("?origin=PREVIOUS_APPLICATIONS", originQuery);
    }

    @Test
    public void markApplicationAsSuccessful() throws Exception {

        setLoggedInUser(newUserResource()
                .withRolesGlobal(singletonList(Role.IFS_ADMINISTRATOR))
                .build());

        ProjectResource projectResource = newProjectResource()
                .withId(1L)
                .withName("Successful project")
                .build();

        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(anyLong(), any(FundingDecision.class), anyListOf(Long.class)))
                .thenReturn(ServiceResult.serviceSuccess());
        when(projectRestService.createProjectFromApplicationId(anyLong()))
                .thenReturn(restSuccess(projectResource));

        mockMvc.perform(post("/competition/1/previous/mark-successful/application/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/{competitionId}/previous"))
                .andReturn();

        verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(anyLong(), any(FundingDecision.class), anyListOf(Long.class));
        verify(projectRestService).createProjectFromApplicationId(anyLong());
        verifyNoMoreInteractions(applicationFundingDecisionService, projectRestService);

    }

    @Override
    protected PreviousCompetitionController supplyControllerUnderTest() {
        return new PreviousCompetitionController();
    }
}
