package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.form.InviteNewAssessorsForm;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationAvailableAssessorsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementApplicationAssessmentProgressControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationAssessmentProgressController> {

    @Spy
    @InjectMocks
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationAvailableAssessorsModelPopulator applicationAvailableAssessorsModelPopulator;

    @Override
    protected CompetitionManagementApplicationAssessmentProgressController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationAssessmentProgressController();
    }

    @Test
    public void applicationProgressDefault() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .build();

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems")
        );

        List<ApplicationAvailableAssessorsRowViewModel> assessors = asList(
                new ApplicationAvailableAssessorsRowViewModel("John Smith", "John's skills", 10, 4, 3),
                new ApplicationAvailableAssessorsRowViewModel("Phil Jones", "Phil's skills", 6, 2, 1));

        ApplicationAvailableAssessorsViewModel expectedAssessors = new ApplicationAvailableAssessorsViewModel(assessors);

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeSortField", "title"))
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("available", expectedAssessors))
                .andExpect(view().name("competition/application-progress"));

        verify(applicationAssessmentSummaryRestService, only()).getApplicationAssessmentSummary(applicationId);
    }

    @Test
    public void availableAssessorsSortedByTotalApplications() throws Exception {
        Long competitionId = 1L;
        Long applicationId = 2L;

        ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = newApplicationAssessmentSummaryResource()
                .withId(applicationId)
                .withName("Progressive Machines")
                .withCompetitionId(competitionId)
                .withCompetitionName("Connected digital additive manufacturing")
                .withPartnerOrganisations(asList("Acme Ltd.", "IO Systems"))
                .build();

        when(applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId)).thenReturn(restSuccess(applicationAssessmentSummaryResource));

        ApplicationAssessmentProgressViewModel expectedModel = new ApplicationAssessmentProgressViewModel(
                applicationId,
                "Progressive Machines",
                competitionId,
                "Connected digital additive manufacturing",
                asList("Acme Ltd.", "IO Systems")
        );

        List<ApplicationAvailableAssessorsRowViewModel> assessors = asList(
                new ApplicationAvailableAssessorsRowViewModel("Phil Jones", "Phil's skills", 6, 2, 1),
                new ApplicationAvailableAssessorsRowViewModel("John Smith", "John's skills", 10, 4, 3));

        ApplicationAvailableAssessorsViewModel expectedAssessors = new ApplicationAvailableAssessorsViewModel(assessors);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors?sort=totalApplications", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("activeSortField", "totalApplications"))
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attribute("available", expectedAssessors))
                .andExpect(view().name("competition/application-progress"))
                .andReturn();

       // ApplicationAvailableAssessorsViewModel returnedModel = (ApplicationAvailableAssessorsViewModel) result.getModelAndView().getModel().get("available");
       // assertEquals(expectedAssessors, returnedModel);

        verify(applicationAssessmentSummaryRestService, only()).getApplicationAssessmentSummary(applicationId);
    }

}