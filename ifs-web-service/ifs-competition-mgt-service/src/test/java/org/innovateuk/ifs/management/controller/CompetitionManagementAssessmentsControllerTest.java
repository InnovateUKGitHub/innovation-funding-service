package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.model.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ManageAssessmentsViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementAssessmentsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessmentsController> {

    @InjectMocks
    @Spy
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @Override
    protected CompetitionManagementAssessmentsController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentsController();
    }

    @Test
    public void manageAssessments() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withName("Test Competition")
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        MvcResult result = mockMvc.perform(get("/assessment/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/manage-assessments"))
                .andReturn();

        ManageAssessmentsViewModel model = (ManageAssessmentsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competitionResource.getId().longValue(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());
        assertTrue(model.isInAssessment());
    }
}
