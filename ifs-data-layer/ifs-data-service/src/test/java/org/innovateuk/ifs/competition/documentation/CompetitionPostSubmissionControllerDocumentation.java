package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.competition.controller.CompetitionPostSubmissionController;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.documentation.CompetitionOpenQueryResourceDocs;
import org.innovateuk.ifs.documentation.SpendProfileStatusResourceDocs;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionPostSubmissionControllerDocumentation extends BaseControllerMockMVCTest<CompetitionPostSubmissionController> {
    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationNotificationService applicationNotificationServiceMock;

    @Override
    protected CompetitionPostSubmissionController supplyControllerUnderTest() {
        return new CompetitionPostSubmissionController();
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationNotificationServiceMock.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/release-feedback", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition for the notifications")
                        ))
                );
    }

    @Test
    public void getOpenQueryCount() throws Exception {
        when(competitionService.countAllOpenQueries(321L)).thenReturn(serviceSuccess(1L));

        mockMvc.perform(get("/competition/post-submission/{id}/queries/open/count", 321L)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

        mockMvc.perform(get("/competition/post-submission/{id}/queries/open", 321L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition whose open queries are being retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of open queries")
                        ).andWithPrefix("[].", CompetitionOpenQueryResourceDocs.competitionOpenQueryFields)
                ));
    }

    @Test
    public void getPendingSpendProfiles() throws Exception {

        final Long competitionId = 1L;

        SpendProfileStatusResource resource1 = new SpendProfileStatusResource(11L, 1L, "Project Name 1");
        SpendProfileStatusResource resource2 = new SpendProfileStatusResource(11L, 2L, "Project Name 2");
        List<SpendProfileStatusResource> pendingSpendProfiles = Arrays.asList(resource1, resource2);
        when(competitionService.getPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfiles));

        mockMvc.perform(get("/competition/post-submission/{competitionId}/pending-spend-profiles", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfiles)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition, whose Projects, which are pending Spend Profile generation, are being retrieved")
                        )
                        ,
                        responseFields(
                                fieldWithPath("[]").description("List of projects for which Spend Profile generation is pending, for a given competition")
                        ).andWithPrefix("[].", SpendProfileStatusResourceDocs.spendProfileStatusFields)
                ));

        verify(competitionService, only()).getPendingSpendProfiles(competitionId);
    }

    @Test
    public void countPendingSpendProfiles() throws Exception {

        final Long competitionId = 1L;
        final Long pendingSpendProfileCount = 3L;

        when(competitionService.countPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfileCount));

        mockMvc.perform(get("/competition/post-submission/{competitionId}/count-pending-spend-profiles", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfileCount)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition, whose count of Projects, which are pending Spend Profile generation, is being retrieved")
                        )
                ));

        verify(competitionService, only()).countPendingSpendProfiles(competitionId);
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 2L;
        when(competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/close-assessment", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to close the assessment of")
                        )
                ));
    }

    @Test
    public void reopenAssessment() throws Exception {
        long competitionId = 2L;
        when(competitionService.reopenAssessmentPeriod(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/reopen-assessment-period", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to reopen the assessment period of")
                        )
                ));
    }
}
