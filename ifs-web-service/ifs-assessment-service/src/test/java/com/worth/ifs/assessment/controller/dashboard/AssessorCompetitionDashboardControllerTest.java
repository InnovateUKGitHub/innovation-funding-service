package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorCompetitionDashboardModelPopulator;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionDashboardController> {

    @Spy
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Override
    protected AssessorCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionDashboardController();
    }

    @Test
    public void competitionDashboard() throws Exception {
        Long competitionId = 1L;

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        List<AssessorCompetitionDashboardApplicationViewModel> expectedApplications = asList(
                new AssessorCompetitionDashboardApplicationViewModel(8L, 9L, "Juggling is fun", "The Best Juggling Company"),
                new AssessorCompetitionDashboardApplicationViewModel(14L, 10L, "Juggling is word that sounds funny to say", "Mo Juggling Mo Problems Ltd")
        );

        AssessorCompetitionDashboardViewModel model = (AssessorCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Juggling Craziness", model.getCompetitionTitle());
        assertEquals("Juggling Craziness (CRD3359)", model.getCompetition());
        assertEquals("Innovate UK", model.getFundingBody());
        assertEquals(expectedApplications, model.getApplications());
    }
}