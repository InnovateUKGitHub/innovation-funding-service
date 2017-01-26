package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
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
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

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


        List<MilestoneResource> milestoneResources = newMilestoneResource()
               .withId(1L)
                 .withName(MilestoneType.OPEN_DATE)
                .withDate(LocalDateTime.now())
                .withCompetitionId(1L).build(1);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId())).thenReturn(restSuccess(3L));
        when(milestoneServiceMock.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(milestoneResources);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-in-flight"))
                .andReturn();

        CompetitionInFlightViewModel model = (CompetitionInFlightViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
        verify(assessmentRestService, only()).countByStateAndCompetition(AssessmentStates.CREATED, competition.getId());
    }

    @Test
    public void competition_closed() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CLOSED)
                .withName("Photonics for health")
                .build();

        List<MilestoneResource> milestoneResources = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(LocalDateTime.now())
                .withCompetitionId(1L).build(1);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId())).thenReturn(restSuccess(3L));
        when(milestoneServiceMock.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(milestoneResources);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-in-flight"))
                .andReturn();

        CompetitionInFlightViewModel model = (CompetitionInFlightViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Photonics for health", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/"+ competitionId));
        verify(competitionService, only()).closeAssessment(competitionId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + competitionId));
        verify(competitionService, only()).notifyAssessors(competitionId);
    }
}
