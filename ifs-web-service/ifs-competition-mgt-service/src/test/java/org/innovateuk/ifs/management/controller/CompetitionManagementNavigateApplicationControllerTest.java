package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.*;
import org.innovateuk.ifs.management.viewmodel.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementNavigateApplicationControllerTest extends BaseControllerMockMVCTest<CompetitionManagementNavigateApplicationsController> {

    private Long COMPETITION_ID = 1L;

    private String COMPETITION_NAME = "comp1";

    @InjectMocks
    @Spy
    private NavigateApplicationsModelPopulator navigateApplicationsModelPopulator;

    @Override
    protected CompetitionManagementNavigateApplicationsController supplyControllerUnderTest() {
        return new CompetitionManagementNavigateApplicationsController();
    }

    @Before
    public void setDefaults() {
    }

    @Test
    public void navigationOptions() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).withName(COMPETITION_NAME).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/navigate", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/navigate-applications"))
                .andReturn();

        NavigateApplicationsViewModel model = (NavigateApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(competitionService, only()).getById(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(COMPETITION_NAME, model.getCompetitionName());
    }

}
