package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.transactional.CompetitionKeyAssessmentStatisticsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionKeyAssessmentStatisticsControllerTest extends
        BaseControllerMockMVCTest<CompetitionKeyAssessmentStatisticsController> {

    @Mock
    private CompetitionKeyAssessmentStatisticsService competitionKeyAssessmentStatisticsServiceMock;

    @Override
    protected CompetitionKeyAssessmentStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyAssessmentStatisticsController();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionReadyToOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionReadyToOpenKeyAssessmentStatisticsResource().build();

        when(competitionKeyAssessmentStatisticsServiceMock.getReadyToOpenKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-assessment-statistics/{id}/ready-to-open", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyAssessmentStatisticsResource().build();
        when(competitionKeyAssessmentStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-assessment-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionClosedKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyAssessmentStatisticsResource().build();
        when(competitionKeyAssessmentStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-assessment-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource().build();
        when(competitionKeyAssessmentStatisticsServiceMock.getInAssessmentKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-assessment-statistics/{id}/in-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }
}