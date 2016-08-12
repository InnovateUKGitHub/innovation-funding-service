package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorDashboardModelPopulator;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorDashboardControllerTest extends BaseControllerMockMVCTest<AssessorDashboardController> {

    @Spy
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @Override
    protected AssessorDashboardController supplyControllerUnderTest() {
        return new AssessorDashboardController();
    }

    @Test
    public void dashboard() throws Exception {

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = asList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 1, 2, LocalDate.parse("2016-12-31"), 16L, 20L)
        );

        assertTrue(model.getInvitations().isEmpty());
        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }
}