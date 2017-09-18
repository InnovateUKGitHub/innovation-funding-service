package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.competition.controller.CompetitionKeyStatisticsController;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentPanelKeyStatisticsResourceDocs.assessmentPanelKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessmentPanelKeyStatisticsResourceDocs.assessmentPanelKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyStatisticsResourceDocs.competitionClosedKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyStatisticsResourceDocs.competitionClosedKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyStatisticsResourceDocs.competitionInAssessmentKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyStatisticsResourceDocs.competitionInAssessmentKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyStatisticsResourceDocs.competitionOpenKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyStatisticsResourceDocs.competitionOpenKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyStatisticsResourceDocs.competitionReadyToOpenKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyStatisticsResourceDocs.competitionReadyToOpenKeyStatisticsResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionKeyStatisticsControllerDocumentation extends BaseControllerMockMVCTest<CompetitionKeyStatisticsController> {

    @Override
    protected CompetitionKeyStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyStatisticsController();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = competitionReadyToOpenKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getReadyToOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competitionStatistics/{id}/readyToOpen", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competitionStatistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionReadyToOpenKeyStatisticsResourceFields)
                ));
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = competitionOpenKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competitionStatistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competitionStatistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionOpenKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = competitionClosedKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competitionStatistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competitionStatistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionClosedKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = competitionInAssessmentKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getInAssessmentKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competitionStatistics/{id}/inAssessment", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competitionStatistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionInAssessmentKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getInAssessmentPanelKeyStatistics() throws Exception {
        long competitionId = 1L;
        AssessmentPanelKeyStatisticsResource assessmentPanelKeyStatisticsResource = assessmentPanelKeyStatisticsResourceBuilder.build();

        when(assessmentServiceMock.getAssessmentPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess(assessmentPanelKeyStatisticsResource));
        mockMvc.perform(get("/competitionStatistics/{id}/panel", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competitionStatistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(assessmentPanelKeyStatisticsResourceFields)
                ));
    }

}
