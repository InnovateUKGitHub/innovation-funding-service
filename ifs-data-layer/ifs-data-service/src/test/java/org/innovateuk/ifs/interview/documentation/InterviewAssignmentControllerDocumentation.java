package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.interview.controller.InterviewAssignmentController;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackService;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationInviteService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AvailableApplicationPageResourceDocs.availableApplicationPageResourceBuilder;
import static org.innovateuk.ifs.documentation.AvailableApplicationPageResourceDocs.availableApplicationPageResourceFields;
import static org.innovateuk.ifs.documentation.AvailableApplicationResourceDocs.availableApplicationResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.stagedApplicationListResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.stagedApplicationResourceFields;
import static org.innovateuk.ifs.documentation.FileEntryDocs.fileEntryResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentApplicationResourceDocs.interviewAssignmentAssignedResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentAssignedPageResourceDocs.interviewAssignmentAssignedPageResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewAssignmentAssignedPageResourceDocs.interviewAssignmentAssignedPageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInvitePageResourceDocs.interviewAssignmentCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInvitePageResourceDocs.interviewAssignmentCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentCreatedInviteResourceDocs.interviewAssignmentCreatedInviteResourceFields;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationSentInviteResourceBuilder.newInterviewApplicationSentInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAssignmentControllerDocumentation extends BaseFileControllerMockMVCTest<InterviewAssignmentController> {

    private static final long competitionId = 1L;

    StagedApplicationListResource stagedApplicationInviteListResource = stagedApplicationListResourceBuilder.build();
    List<StagedApplicationResource> stagedInviteResources = stagedApplicationInviteListResource.getInvites();

    @Mock
    private InterviewAssignmentService interviewAssignmentServiceMock;

    @Mock
    private InterviewApplicationInviteService interviewApplicationInviteServiceMock;

    @Mock
    private InterviewApplicationFeedbackService interviewApplicationFeedbackServiceMock;

    @Override
    public InterviewAssignmentController supplyControllerUnderTest() {
        return new InterviewAssignmentController(null, null, null);
    }

    @Test
    public void assignApplication() throws Exception {
        when(interviewAssignmentServiceMock.assignApplications(stagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/assign-applications")
                .header("IFS_AUTH_TOKEN", "123abc")
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
        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getAvailableApplications(competitionId, pageable)).thenReturn(serviceSuccess(availableApplicationPageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/available-applications/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
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
        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getStagedApplications(competitionId, pageable)).thenReturn(serviceSuccess(interviewAssignmentCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/staged-applications/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
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
        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        when(interviewAssignmentServiceMock.getAssignedApplications(competitionId, pageable)).thenReturn(serviceSuccess(interviewAssignmentAssignedPageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel/assigned-applications/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
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

        mockMvc.perform(get("/interview-panel/available-application-ids/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

        mockMvc.perform(post("/interview-panel/unstage-application/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
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

        mockMvc.perform(post("/interview-panel/unstage-applications/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
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
        when(interviewApplicationInviteServiceMock.getEmailTemplate()).thenReturn(serviceSuccess(new ApplicantInterviewInviteResource("Content")));

        mockMvc.perform(get("/interview-panel/email-template")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        responseFields(fieldWithPath("content").description("The content of the email template sent to applicants"))
                ));

        verify(interviewApplicationInviteServiceMock, only()).getEmailTemplate();
    }

    @Test
    public void sendInvites() throws Exception {
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");
        when(interviewApplicationInviteServiceMock.sendInvites(competitionId, sendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/send-invites/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sendResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to send invites of")
                        ),
                        requestFields(
                                fieldWithPath("subject").description("Subject of the email to send to applicants"),
                                fieldWithPath("content").description("Content of the email to send to applicants")
                        )
                ));

        verify(interviewApplicationInviteServiceMock, only()).sendInvites(competitionId, sendResource);
    }

    @Test
    public void isApplicationAssigned() throws Exception {
        long applicationId = 1L;
        when(interviewAssignmentServiceMock.isApplicationAssigned(applicationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/interview-panel/is-assigned/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to check")
                        )
                ));

        verify(interviewAssignmentServiceMock, only()).isApplicationAssigned(applicationId);
    }

    @Test
    public void findFeedback() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(interviewApplicationFeedbackServiceMock.findFeedback(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/interview-panel/feedback-details/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)))
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("Id of the Attachment to be fetched")),
                        responseFields(fileEntryResourceFields)));

        verify(interviewApplicationFeedbackServiceMock).findFeedback(applicationId);
    }

    @Test
    public void downloadFeedback() throws Exception {
        final long applicationId = 22L;

        Function<InterviewApplicationFeedbackService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadFeedback(applicationId);

        assertGetFileContents("/interview-panel/feedback/{applicationId}", new Object[]{applicationId},
                emptyMap(), interviewApplicationFeedbackServiceMock, serviceCallToDownload)
                .andExpect(status().isOk())
                .andDo(documentFileGetContentsMethod("interview-panel/{method-name}"));
    }

    @Test
    public void deleteFeedback() throws Exception {
        final long applicationId = 22L;
        when(interviewApplicationFeedbackServiceMock.deleteFeedback(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/interview-panel/feedback/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("Id of the application to have attachment deleted")))
                );

        verify(interviewApplicationFeedbackServiceMock).deleteFeedback(applicationId);
    }

    @Test
    public void uploadFeedback() throws Exception {
        final long applicationId = 77L;
        when(interviewApplicationFeedbackServiceMock.uploadFeedback(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/feedback/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(parameterWithName("applicationId").description("The application in which the feedback will be attached.")),
                        requestParameters(parameterWithName("filename").description("The filename of the file being uploaded")),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf")
                        )
                ));

        verify(interviewApplicationFeedbackServiceMock).uploadFeedback(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class));
    }

    @Test
    public void getSentInvite() throws Exception {
        long applicationId = 1L;
        InterviewApplicationSentInviteResource sentInvite = newInterviewApplicationSentInviteResource()
                .withSubject("Subject")
                .withContent("content")
                .withAssigned(ZonedDateTime.now())
                .build();

        when(interviewApplicationInviteServiceMock.getSentInvite(applicationId)).thenReturn(serviceSuccess(sentInvite));

        mockMvc.perform(get("/interview-panel/sent-invite/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to get invite of")
                        ),
                        responseFields(
                                fieldWithPath("subject").type("String").description("Subject of the invite"),
                                fieldWithPath("content").type("String").description("Content of the message sent"),
                                fieldWithPath("assigned").type("Date").description("The date the message was sent")
                        )
                ));

        verify(interviewApplicationInviteServiceMock, only()).getSentInvite(applicationId);
    }

    @Test
    public void resendInvite() throws Exception {
        long applicationId = 1L;
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");
        when(interviewApplicationInviteServiceMock.resendInvite(applicationId, sendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/resend-invite/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sendResource)))
                .andExpect(status().isOk())
                .andDo(document("interview-panel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to resend invite of")
                        ),
                        requestFields(
                                fieldWithPath("subject").description("Subject of the email to send to applicants"),
                                fieldWithPath("content").description("Content of the email to send to applicants")
                        )
                ));

        verify(interviewApplicationInviteServiceMock, only()).resendInvite(applicationId, sendResource);
    }

    private HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}
