package com.worth.ifs.management.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.model.CompetitionClosedModelPopulator;
import com.worth.ifs.management.model.CompetitionInAssessmentModelPopulator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementCompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementCompetitionController> {

    @Mock
    private CompetitionService competitionService;

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

    @Ignore
    @Test
    public void competition() throws Exception {
        Long competitionId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .build();

        when(competitionService.getById(competitionId)).thenReturn(competition);

        mockMvc.perform(get("/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-closed"));

        verify(competitionService, only()).getById(competition.getId());
    }
}