package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionClosedModelPopulator;
import org.innovateuk.ifs.management.model.CompetitionInAssessmentModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionClosedViewModel;
import org.innovateuk.ifs.management.viewmodel.CompetitionInAssessmentViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementCompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementCompetitionController> {

    @Spy
    @InjectMocks
    private CompetitionInAssessmentModelPopulator competitionInAssessmentModelPopulator;

    @Spy
    @InjectMocks
    private CompetitionClosedModelPopulator competitionClosedModelPopulator;

    @Override
    protected CompetitionManagementCompetitionController supplyControllerUnderTest() {
        return new CompetitionManagementCompetitionController();
    }

    @Test
    public void competition_inAssessment() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-in-assessment"))
                .andReturn();

        CompetitionInAssessmentViewModel model = (CompetitionInAssessmentViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

    @Test
    public void competition_closed() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CLOSED)
                .withName("Photonics for health")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-closed"))
                .andReturn();

        CompetitionClosedViewModel model = (CompetitionClosedViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Photonics for health", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
        verify(competitionService, only()).closeAssessment(competitionId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
        verify(competitionService, only()).notifyAssessors(competitionId);
    }
}
