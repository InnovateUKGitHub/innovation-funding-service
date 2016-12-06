package com.worth.ifs.management.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.model.InviteAssessorsFindModelPopulator;
import com.worth.ifs.management.model.InviteAssessorsInviteModelPopulator;
import com.worth.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import com.worth.ifs.management.viewmodel.InviteAssessorsFindViewModel;
import com.worth.ifs.management.viewmodel.InviteAssessorsInviteViewModel;
import com.worth.ifs.management.viewmodel.InviteAssessorsOverviewViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementInviteAssessorsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementInviteAssessorsController> {

    @Spy
    @InjectMocks
    private InviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Spy
    @InjectMocks
    private InviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Spy
    @InjectMocks
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @Override
    protected CompetitionManagementInviteAssessorsController supplyControllerUnderTest() {
        return new CompetitionManagementInviteAssessorsController();
    }

    @Test
    public void assessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(get("/competition/{competitionId}/assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/find", competitionId)));
    }

    @Test
    public void find() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/find", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/find"))
                .andReturn();

        InviteAssessorsFindViewModel model = (InviteAssessorsFindViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

    @Test
    public void invite() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/invite", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/invite"))
                .andReturn();

        InviteAssessorsInviteViewModel model = (InviteAssessorsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

    @Test
    public void overview() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .build();

        when(competitionService.getById(competition.getId())).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/overview", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/overview"))
                .andReturn();

        InviteAssessorsOverviewViewModel model = (InviteAssessorsOverviewViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());

        verify(competitionService, only()).getById(competition.getId());
    }

}