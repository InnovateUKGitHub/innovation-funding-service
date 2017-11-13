package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.controller.CompetitionPostSubmissionController;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionPendingSpendProfilesResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Override
    protected CompetitionPostSubmissionController supplyControllerUnderTest() {
        return new CompetitionPostSubmissionController();
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/notify-assessors", competitionId))
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

        mockMvc.perform(put("/competition/postSubmission/{id}/release-feedback", competitionId))
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

        mockMvc.perform(get("/competition/postSubmission/feedback-released"))
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

        mockMvc.perform(get("/competition/postSubmission/{id}/queries/open/count", 321L))
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

        mockMvc.perform(get("/competition/postSubmission/{id}/queries/open", 321L))
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

    @Test
    public void getPendingSpendProfiles() throws Exception {

        final Long competitionId = 1L;

        CompetitionPendingSpendProfilesResource resource1 = new CompetitionPendingSpendProfilesResource(11L, 1L, "Project Name 1");
        CompetitionPendingSpendProfilesResource resource2 = new CompetitionPendingSpendProfilesResource(11L, 2L, "Project Name 2");
        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = Arrays.asList(resource1, resource2);
        when(competitionService.getPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfiles));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/pending-spend-profiles", competitionId))
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
                        )
                ));

        verify(competitionService, only()).getPendingSpendProfiles(competitionId);
    }

    @Test
    public void countPendingSpendProfiles() throws Exception {

        final Long competitionId = 1L;
        final Long pendingSpendProfileCount = 3L;

        when(competitionService.countPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfileCount));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/count-pending-spend-profiles", competitionId))
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
    public void findUnsuccessfulApplications() throws Exception {
        final Long competitionId = 1L;
        int pageIndex = 0;
        int pageSize = 20;
        String sortField = "id";

        ApplicationPageResource applicationPage = new ApplicationPageResource();

        when(competitionService.findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField)).thenReturn(serviceSuccess(applicationPage));

        mockMvc.perform(get("/competition/postSubmission/{id}/unsuccessful-applications?page={page}&size={pageSize}&sort={sortField}", competitionId, pageIndex, pageSize, sortField))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationPage)))
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The competition for which unsuccessful applications need to be found")
                        )
                ));

        verify(competitionService, only()).findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField);

    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 2L;
        when(competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andDo(document(
                        "competition/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the competition to close the assessment of")
                        )
                ));
    }
}
