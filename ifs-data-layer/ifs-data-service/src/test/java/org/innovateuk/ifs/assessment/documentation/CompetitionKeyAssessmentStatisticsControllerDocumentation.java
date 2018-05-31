package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.CompetitionKeyAssessmentStatisticsController;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.transactional.CompetitionKeyAssessmentStatisticsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyAssessmentStatisticsResourceDocs.competitionClosedKeyAssessmentStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyAssessmentStatisticsResourceDocs.competitionClosedKeyAssessmentStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyAssessmentStatisticsResourceDocs.competitionInAssessmentKeyAssessmentStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyAssessmentStatisticsResourceDocs.competitionInAssessmentKeyAssessmentStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyAssessmentStatisticsResourceDocs.competitionOpenKeyAssessmentStatisticsResourceBuilder;

import static org.innovateuk.ifs.documentation.CompetitionOpenKeyAssessmentStatisticsResourceDocs
        .competitionOpenKeyAssessmentStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyAssessmentStatisticsResourceDocs.competitionReadyToOpenKeyAssessmentStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyAssessmentStatisticsResourceDocs.competitionReadyToOpenKeyAssessmentStatisticsResourceFields;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionKeyAssessmentStatisticsControllerDocumentation extends
        BaseControllerMockMVCTest<CompetitionKeyAssessmentStatisticsController> {

    @Mock
    private CompetitionKeyAssessmentStatisticsService competitionKeyAssessmentStatisticsService;

    @Override
    protected CompetitionKeyAssessmentStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyAssessmentStatisticsController();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionReadyToOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                competitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.build();

        when(competitionKeyAssessmentStatisticsService.getReadyToOpenKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-assessment-statistics/{id}/ready-to-open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionReadyToOpenKeyAssessmentStatisticsResourceFields)
                ));

        verify(competitionKeyAssessmentStatisticsService, only()).getReadyToOpenKeyStatisticsByCompetition
                (competitionId);
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                competitionOpenKeyAssessmentStatisticsResourceBuilder
                        .build();

        when(competitionKeyAssessmentStatisticsService.getOpenKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-assessment-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionOpenKeyAssessmentStatisticsResourceFields)
                ));

        verify(competitionKeyAssessmentStatisticsService, only()).getOpenKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionClosedKeyAssessmentStatisticsResource keyStatisticsResource =
                competitionClosedKeyAssessmentStatisticsResourceBuilder.build();

        when(competitionKeyAssessmentStatisticsService.getClosedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-assessment-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionClosedKeyAssessmentStatisticsResourceFields)
                ));

        verify(competitionKeyAssessmentStatisticsService, only()).getClosedKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource =
                competitionInAssessmentKeyAssessmentStatisticsResourceBuilder.build();

        when(competitionKeyAssessmentStatisticsService.getInAssessmentKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-assessment-statistics/{id}/in-assessment", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionInAssessmentKeyAssessmentStatisticsResourceFields)
                ));

        verify(competitionKeyAssessmentStatisticsService, only()).getInAssessmentKeyStatisticsByCompetition
                (competitionId);
    }
}