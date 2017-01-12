package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.management.model.ApplicationAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementApplicationAssessmentProgressControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationAssessmentProgressController> {

    @Spy
    @InjectMocks
    private ApplicationAssessmentProgressModelPopulator applicationAssessmentProgressModelPopulator;

    @Override
    protected CompetitionManagementApplicationAssessmentProgressController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationAssessmentProgressController();
    }

    @Test
    public void applicationProgress() throws Exception {
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

        mockMvc.perform(get("/competition/{competitionId}/application/{applicationId}/assessors", competitionId, applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/application-progress"));

        verify(applicationAssessmentSummaryRestService, only()).getApplicationAssessmentSummary(applicationId);
    }
}