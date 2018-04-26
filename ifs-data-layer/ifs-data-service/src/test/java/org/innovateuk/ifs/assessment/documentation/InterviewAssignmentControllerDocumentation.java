package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.controller.InterviewAssignmentController;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
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
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.stagedApplicationListResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.stagedApplicationResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentApplicationResourceDocs.interviewAssignmentAssignedResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentAssignedPageResourceDocs.*;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInvitePageResourceDocs.interviewAssignmentCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInvitePageResourceDocs.interviewAssignmentCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInviteResourceDocs.interviewAssignmentCreatedInviteResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAssignmentControllerDocumentation extends BaseControllerMockMVCTest<InterviewAssignmentController> {

    private static final long competitionId = 1L;

    StagedApplicationListResource stagedApplicationInviteListResource = stagedApplicationListResourceBuilder.build();
    List<StagedApplicationResource> stagedInviteResources = stagedApplicationInviteListResource.getInvites();

    @Override
    public InterviewAssignmentController supplyControllerUnderTest() {
        return new InterviewAssignmentController(null);
    }

    @Test
    public void assignApplication() throws Exception {
        when(interviewAssignmentServiceMock.assignApplications(stagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/assign-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(stagedApplicationInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of applications to be invited to the interview panel")
                        ).andWithPrefix("invites[].", stagedApplicationResourceFields)
                ));

        verify(interviewAssignmentServiceMock, only()).assignApplications(stagedInviteResources);
    }

    @Test
    public void getAvailableApplications() throws Exception {
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getAvailableApplications(competitionId, pageable)).thenReturn(serviceSuccess(availableApplicationPageResourceBuilder.build()));

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

        verify(interviewAssignmentServiceMock, only()).getAvailableApplications(competitionId, pageable);
    }

    @Test
    public void getStagedApplications() throws Exception {
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getStagedApplications(competitionId, pageable)).thenReturn(serviceSuccess(interviewAssignmentCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/staged-applications/{competitionId}", 1L)
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
                        responseFields(interviewAssignmentCreatedInvitePageResourceFields)
                                .andWithPrefix("content[].", interviewAssignmentCreatedInviteResourceFields)
                ));

        verify(interviewAssignmentServiceMock, only()).getStagedApplications(competitionId, pageable);
    }

    @Test
    public void getAssignedApplications() throws Exception {
        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getAssignedApplications(competitionId, pageable)).thenReturn(serviceSuccess(interviewAssignmentAssignedPageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/assigned-applications/{competitionId}", 1L)
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
                        responseFields(interviewAssignmentAssignedPageResourceFields)
                                .andWithPrefix("content[].", interviewAssignmentAssignedResourceFields)
                ));

        verify(interviewAssignmentServiceMock, only()).getAssignedApplications(competitionId, pageable);
    }

    @Test
    public void getAvailableApplicationIds() throws Exception {
        when(interviewAssignmentServiceMock.getAvailableApplicationIds(competitionId)).thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/interview-panel/available-application-ids/{competitionId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[].").description("List of available application ids"))
                ));

        verify(interviewAssignmentServiceMock, only()).getAvailableApplicationIds(competitionId);
    }

    @Test
    public void unstageApplication() throws Exception {
        long applicationId = 123L;
        when(interviewAssignmentServiceMock.unstageApplication(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/unstage-application/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to unstage")
                        )
                ));

        verify(interviewAssignmentServiceMock, only()).unstageApplication(applicationId);
    }

    @Test
    public void unstageApplications() throws Exception {
        long competitionId = 123L;
        when(interviewAssignmentServiceMock.unstageApplications(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/unstage-applications/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to unstage all applications on")
                        )
                ));

        verify(interviewAssignmentServiceMock, only()).unstageApplications(competitionId);
    }

    @Test
    public void getEmailTemplate() throws Exception {
        when(interviewAssignmentServiceMock.getEmailTemplate()).thenReturn(serviceSuccess(new ApplicantInterviewInviteResource("Content")));

        mockMvc.perform(get("/interview-panel/email-template"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        responseFields(fieldWithPath("content").description("The content of the email template sent to applicants"))
                ));

        verify(interviewAssignmentServiceMock, only()).getEmailTemplate();
    }

    @Test
    public void sendInvites() throws Exception {
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");
        when(interviewAssignmentServiceMock.sendInvites(competitionId, sendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/send-invites/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sendResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        requestFields(
                                fieldWithPath("subject").description("Subject of the email to send to applicants"),
                                fieldWithPath("content").description("Content of the email to send to applicants")
                        )
                ));

        verify(interviewAssignmentServiceMock, only()).sendInvites(competitionId, sendResource);
    }

    @Test
    public void isApplicationAssigned() throws Exception {
        long applicationId = 1L;
        when(interviewAssignmentServiceMock.isApplicationAssigned(applicationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/interview-panel/is-assigned/{applicationId}", applicationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to check")
                        )
                ));

        verify(interviewAssignmentServiceMock, only()).isApplicationAssigned(applicationId);
    }
}