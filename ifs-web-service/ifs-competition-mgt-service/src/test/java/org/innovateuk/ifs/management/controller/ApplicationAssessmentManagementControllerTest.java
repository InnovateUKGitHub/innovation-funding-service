package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.ManageApplicationsPopulator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationAssessmentManagementControllerTest extends BaseControllerMockMVCTest<ApplicationAssessmentManagementController> {

    @Mock
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @InjectMocks
    @Spy
    private ManageApplicationsPopulator manageApplicationsPopulator;

    @Override
    protected ApplicationAssessmentManagementController supplyControllerUnderTest() {
        return new ApplicationAssessmentManagementController();
    }

    @Test
    public void testManageApplications() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withName("name").build();

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource().build(2);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(summaryResources));

        mockMvc.perform(get("/assessment/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-applications"));
    }
}
