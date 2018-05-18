package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.resource.InterviewNotifyAllocationResource;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewAllocationControllerTest extends BaseControllerMockMVCTest<InterviewAllocationController> {

    @Mock
    private InterviewAllocationService interviewAllocationServiceMock;

    @Override
    protected InterviewAllocationController supplyControllerUnderTest() {
        return new InterviewAllocationController();
    }

    @Test
    public void getInterviewAcceptedAssessors() throws Exception {
        long competitionId = 1L;
        int page = 2;
        int size = 10;

        InterviewAcceptedAssessorsPageResource expectedPageResource = newInterviewAcceptedAssessorsPageResource()
                .withContent(newInterviewAcceptedAssessorsResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.email"));

        when(interviewAllocationServiceMock.getInterviewAcceptedAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/allocate-assessors/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getInterviewAcceptedAssessors(competitionId, pageable);
    }

    @Test
    public void getAllocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;
        int page = 2;
        int size = 10;

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(newInterviewApplicationResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "target.id"));

        when(interviewAllocationServiceMock.getAllocatedApplicationsById(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getAllocatedApplicationsById(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplications() throws Exception {
        long competitionId = 1L;
        long userId = 2L;
        int page = 2;
        int size = 10;

        InterviewApplicationPageResource expectedPageResource = newInterviewApplicationPageResource()
                .withContent(newInterviewApplicationResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "target.id"));

        when(interviewAllocationServiceMock.getUnallocatedApplications(competitionId, userId, pageable))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-applications/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplications(competitionId, userId, pageable);
    }

    @Test
    public void getUnallocatedApplicationIds() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        List<Long> ids = asList(1L);

        when(interviewAllocationServiceMock.getUnallocatedApplicationIds(competitionId, userId))
                .thenReturn(serviceSuccess(ids));

        mockMvc.perform(get("/interview-panel/{competitionId}/unallocated-application-ids/{userId}", competitionId, userId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "target.id"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(ids)));

        verify(interviewAllocationServiceMock, only()).getUnallocatedApplicationIds(competitionId, userId);
    }

    @Test
    public void getAllocatedApplicationsById() throws Exception {
        long competitionId = 1L;
        List<Long> applicationIds = asList(1L,2L,3L);

        List<InterviewApplicationResource> interviewApplicationResources = newInterviewApplicationResource().build(2);

        when(interviewAllocationServiceMock.getAllocatedApplicationsById(applicationIds)).thenReturn(serviceSuccess(interviewApplicationResources));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/all/{applicationIds}",
                                    competitionId, String.join(",", simpleJoiner(applicationIds, ","))))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewApplicationResources)));

        verify(interviewAllocationServiceMock, only()).getAllocatedApplicationsById(applicationIds);
    }

    @Test
    public void getInviteToSend() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        AssessorInvitesToSendResource assessorInvitesToSendResource = newAssessorInvitesToSendResource().build();

        when(interviewAllocationServiceMock.getInviteToSend(competitionId, userId)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/interview-panel/{competitionId}/allocated-applications/{userId}/invite-to-send", competitionId, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessorInvitesToSendResource)));

        verify(interviewAllocationServiceMock, only()).getInviteToSend(competitionId, userId);
    }

    @Test
    public void sendInvite() throws Exception {
        long competitionId = 1L;
        long userId = 2L;

        InterviewNotifyAllocationResource allocationResource = newInterviewNotifyAllocationResource().build();

        when(interviewAllocationServiceMock.notifyAllocation(allocationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel/{competitionId}/allocated-applications/{assessorId}/send-invite", competitionId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(allocationResource)))
                .andExpect(status().isOk());

        verify(interviewAllocationServiceMock).notifyAllocation(allocationResource);
    }

    @Test
    public void unAssignApplication() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long applicationId = 3L;

        when(interviewAllocationServiceMock.unallocateApplication(competitionId, assessorId, applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(RestDocumentationRequestBuilders.post("/interview-panel/{competitionId}/allocated-applications/{assessorId}/unallocate/{applicationId}", competitionId, assessorId, applicationId))
                .andExpect(status().isOk());

        verify(interviewAllocationServiceMock, only()).unallocateApplication(competitionId, assessorId, applicationId);
    }
}