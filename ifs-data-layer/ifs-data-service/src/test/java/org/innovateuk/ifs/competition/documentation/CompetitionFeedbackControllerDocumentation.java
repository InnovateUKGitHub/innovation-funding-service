package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionFeedbackController;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionFeedbackControllerDocumentation extends BaseControllerMockMVCTest<CompetitionFeedbackController> {
    @Mock
    private CompetitionService competitionService;

    @Override
    protected CompetitionFeedbackController supplyControllerUnderTest() {
        return new CompetitionFeedbackController();
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/feedback/{id}/notify-assessors", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationServiceMock.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/feedback/{id}/release-feedback", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void feedbackReleased() throws Exception {
        when(competitionService.findFeedbackReleasedCompetitions()).thenReturn(serviceSuccess(newCompetitionSearchResultItem().build(2)));

        mockMvc.perform(get("/competition/feedback/feedback-released"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("list of competitions, which have had feedback released, that the authenticated user has access to")
                        )
                ));
    }

    @Test
    public void getOpenQueryCount() throws Exception {
        when(competitionService.countAllOpenQueries(321L)).thenReturn(serviceSuccess(1L));

        mockMvc.perform(get("/competition/{id}/queries/open/count", 321L))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition whose open queries are being counted")
                        )
                ));
    }

    @Test
    public void getOpenQueries() throws Exception {
        when(competitionService.findAllOpenQueries(321L)).thenReturn(serviceSuccess(Arrays.asList(
                new CompetitionOpenQueryResource(1L, 2L, "a", 3L, "b"),
                new CompetitionOpenQueryResource(1L, 2L, "a", 3L, "b"))));

        mockMvc.perform(get("/competition/{id}/queries/open", 321L))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition whose open queries are being retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of open queries")
                        )
                ));
    }


}
