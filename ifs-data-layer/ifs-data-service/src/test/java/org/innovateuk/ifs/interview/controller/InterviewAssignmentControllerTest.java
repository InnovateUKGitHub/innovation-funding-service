package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackService;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Function;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationPageResourceBuilder.newAvailableApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewAssignmentCreatedInviteResourceBuilder.newInterviewAssignmentStagedApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewAssignmentStagedApplicationPageResourceBuilder.newInterviewAssignmentStagedApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationListResourceBuilder.newStagedApplicationListResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAssignmentControllerTest extends BaseControllerMockMVCTest<InterviewAssignmentController> {

    private static final long COMPETITION_ID = 1L;

    @Override
    protected InterviewAssignmentController supplyControllerUnderTest() {
        return new InterviewAssignmentController(null, null, null);
    }

    @Test
    public void getAvailableApplications() throws Exception {
        int page = 5;
        int pageSize = 30;

        List<AvailableApplicationResource> expectedAvailableApplicationResources = newAvailableApplicationResource().build(2);

        AvailableApplicationPageResource expectedAvailableApplications = newAvailableApplicationPageResource()
                .withContent(expectedAvailableApplicationResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "id"));

        when(interviewAssignmentServiceMock.getAvailableApplications(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableApplications));

        mockMvc.perform(get("/interview-panel/available-applications/{competition-id}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableApplications)));

        verify(interviewAssignmentServiceMock, only()).getAvailableApplications(COMPETITION_ID, pageable);
    }

    @Test
    public void getStagedApplications() throws Exception {
        int page = 5;
        int pageSize = 30;

        List<InterviewAssignmentStagedApplicationResource> expectedStagedApplicationResources = newInterviewAssignmentStagedApplicationResource().build(2);

        InterviewAssignmentStagedApplicationPageResource expectedStagedApplications = newInterviewAssignmentStagedApplicationPageResource()
                .withContent(expectedStagedApplicationResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "id"));

        when(interviewAssignmentServiceMock.getStagedApplications(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedStagedApplications));

        mockMvc.perform(get("/interview-panel/staged-applications/{competition-id}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedStagedApplications)));

        verify(interviewAssignmentServiceMock, only()).getStagedApplications(COMPETITION_ID, pageable);

    }

    @Test
    public void getAvailableApplicationIds() throws Exception {
        List<Long> expectedAvailableApplicationIds = asList(1L, 2L);

        when(interviewAssignmentServiceMock.getAvailableApplicationIds(COMPETITION_ID))
                .thenReturn(serviceSuccess(expectedAvailableApplicationIds));

        mockMvc.perform(get("/interview-panel/available-application-ids/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableApplicationIds)));

        verify(interviewAssignmentServiceMock, only()).getAvailableApplicationIds(COMPETITION_ID);

    }

    @Test
    public void assignApplications() throws Exception {
        StagedApplicationListResource applications = newStagedApplicationListResource()
                .withInvites(newStagedApplicationResource().build(2))
                .build();

        when(interviewAssignmentServiceMock.assignApplications(applications.getInvites())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/assign-applications")
                .contentType(APPLICATION_JSON)
                .content(toJson(applications)))
                .andExpect(status().isOk());

        verify(interviewAssignmentServiceMock, only()).assignApplications(applications.getInvites());
    }

    @Test
    public void unstageApplication() throws Exception {
        long applicationId = 1L;

        when(interviewAssignmentServiceMock.unstageApplication(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/unstage-application/{applicationId}", applicationId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(interviewAssignmentServiceMock, only()).unstageApplication(applicationId);
    }

    @Test
    public void unstageApplications() throws Exception {
        long competitionId = 1L;
        when(interviewAssignmentServiceMock.unstageApplications(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/unstage-applications/{competitionId}", competitionId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(interviewAssignmentServiceMock, only()).unstageApplications(competitionId);
    }

    @Test
    public void getEmailTemplate() throws Exception {
        ApplicantInterviewInviteResource interviewInviteResource = new ApplicantInterviewInviteResource("content");

        when(interviewApplicationInviteService.getEmailTemplate()).thenReturn(serviceSuccess(interviewInviteResource));

        mockMvc.perform(get("/interview-panel/email-template")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewInviteResource)));

        verify(interviewApplicationInviteService, only()).getEmailTemplate();
    }

    @Test
    public void sendInvites() throws Exception {
        long competitionId = 1L;
        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");

        when(interviewApplicationInviteService.sendInvites(competitionId, sendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/send-invites/{competitionId}", competitionId)
                .contentType(APPLICATION_JSON)
                .content(toJson(sendResource)))
                .andExpect(status().isOk());

        verify(interviewApplicationInviteService, only()).sendInvites(competitionId, sendResource);
    }

    @Test
    public void isApplicationAssigned() throws Exception {
        long applicationId = 1L;
        when(interviewAssignmentServiceMock.isApplicationAssigned(applicationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/interview-panel/is-assigned/{applicationId}", applicationId)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(interviewAssignmentServiceMock, only()).isApplicationAssigned(applicationId);
    }

    @Test
    public void testUploadFeedback() throws Exception {
        final long applicationId = 77L;
        when(interviewApplicationFeedbackService.uploadFeedback(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/feedback/{applicationId}", applicationId)
                .param("filename", "randomFile.pdf")
                .headers(createFileUploadHeader("application/pdf", 1234)))
                .andExpect(status().isCreated());

        verify(interviewApplicationFeedbackService).uploadFeedback(eq("application/pdf"), eq("1234"), eq("randomFile.pdf"),
                eq(applicationId), any(HttpServletRequest.class));
    }

    @Test
    public void testDeleteFeedback() throws Exception {
        final long applicationId = 22L;
        when(interviewApplicationFeedbackService.deleteFeedback(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel/feedback/{applicationId}", applicationId))
                .andExpect(status().isNoContent());

        verify(interviewApplicationFeedbackService).deleteFeedback(applicationId);
    }

    @Test
    public void testDownloadFeedback() throws Exception {
        final long applicationId = 22L;

        Function<InterviewApplicationFeedbackService, ServiceResult<FileAndContents>> serviceCallToDownload =
                (service) -> service.downloadFeedback(applicationId);

        assertGetFileContents("/interview-panel/feedback/{applicationId}", new Object[]{applicationId},
                emptyMap(), interviewApplicationFeedbackService, serviceCallToDownload);
    }

    @Test
    public void testFindFeedback() throws Exception {
        final long applicationId = 22L;
        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "application/pdf", 1234);
        when(interviewApplicationFeedbackService.findFeedback(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        mockMvc.perform(get("/interview-panel/feedback-details/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntryResource)));

        verify(interviewApplicationFeedbackService).findFeedback(applicationId);
    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}