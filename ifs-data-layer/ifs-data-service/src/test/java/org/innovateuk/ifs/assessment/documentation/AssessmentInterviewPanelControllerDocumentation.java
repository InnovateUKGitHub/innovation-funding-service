package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.interview.controller.InterviewPanelController;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AvailableApplicationPageResourceDocs.availableApplicationPageResourceBuilder;
import static org.innovateuk.ifs.documentation.AvailableApplicationPageResourceDocs.availableApplicationPageResourceFields;
import static org.innovateuk.ifs.documentation.AvailableApplicationResourceDocs.availableApplicationResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.existingUserStagedInviteListResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.existingUserStagedInviteResourceFields;
import static org.innovateuk.ifs.documentation.InterviewPanelCreatedInvitePageResourceDocs.interviewPanelCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewPanelCreatedInvitePageResourceDocs.interviewPanelCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewPanelCreatedInviteResourceDocs.interviewPanelCreatedInviteResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentInterviewPanelControllerDocumentation extends BaseControllerMockMVCTest<InterviewPanelController> {

    private static final long competitionId = 1L;

    ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
    List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

    @Override
    public InterviewPanelController supplyControllerUnderTest() {
        return new InterviewPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(interviewPanelInviteServiceMock.assignApplications(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/assign-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of existing users to be invited to the interview panel")
                        ).andWithPrefix("invites[].", existingUserStagedInviteResourceFields)
                ));

        verify(interviewPanelInviteServiceMock, only()).assignApplications(existingUserStagedInviteResources);
    }

    @Test
    public void getAvailableApplications() throws Exception {
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(interviewPanelInviteServiceMock.getAvailableApplications(competitionId, pageable)).thenReturn(serviceSuccess(availableApplicationPageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/available-applications/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=name,asc`. Defaults to `name,asc`")
                        ),
                        responseFields(availableApplicationPageResourceFields)
                                .andWithPrefix("content[].", availableApplicationResourceFields)
                ));

        verify(interviewPanelInviteServiceMock, only()).getAvailableApplications(competitionId, pageable);
    }

    @Test
    public void getCreates() throws Exception {
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(interviewPanelInviteServiceMock.getCreatedApplications(competitionId, pageable)).thenReturn(serviceSuccess(interviewPanelCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/invited-applications/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=name,asc`. Defaults to `name,asc`")
                        ),
                        responseFields(interviewPanelCreatedInvitePageResourceFields)
                                .andWithPrefix("content[].", interviewPanelCreatedInviteResourceFields)
                ));

        verify(interviewPanelInviteServiceMock, only()).getCreatedApplications(competitionId, pageable);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        when(interviewPanelInviteServiceMock.getAvailableAssessorIds(competitionId)).thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/interview-panel/available-assessor-ids/{competitionId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[].").description("List of available assessor ids "))
                ));

        verify(interviewPanelInviteServiceMock, only()).getAvailableAssessorIds(competitionId);
    }
}
