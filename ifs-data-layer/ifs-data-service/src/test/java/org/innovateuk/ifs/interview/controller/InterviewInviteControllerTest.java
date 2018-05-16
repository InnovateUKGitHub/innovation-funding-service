package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackService;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationInviteService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.interview.transactional.InterviewInviteService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantResourceBuilder.newInterviewParticipantResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewInviteControllerTest extends BaseControllerMockMVCTest<InterviewInviteController> {

    private static final long COMPETITION_ID = 1L;

    @Mock
    private InterviewInviteService interviewInviteServiceMock;

    @Override
    protected InterviewInviteController supplyControllerUnderTest() {
        return new InterviewInviteController();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        int page = 5;
        int pageSize = 30;

        List<AvailableAssessorResource> expectedAvailableAssessorResources = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expectedAvailableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(expectedAvailableAssessorResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(DESC, "lastName"));

        when(interviewInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessors/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "lastName,desc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(interviewInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
    }

    @Test
    public void getAvailableAssessors_defaultParameters() throws Exception {
        int page = 0;
        int pageSize = 20;

        List<AvailableAssessorResource> expectedAvailableAssessorResources = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expectedAvailableAssessorPageResource = newAvailableAssessorPageResource()
                .withContent(expectedAvailableAssessorResources)
                .withNumber(page)
                .withTotalElements(300L)
                .withTotalPages(10)
                .withSize(30)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "user.firstName", "user.lastName"));

        when(interviewInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessors/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(interviewInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
    }

    @Test
    public void getAvailableAssessorsIds() throws Exception {
        List<Long> expectedAvailableAssessorIds = asList(1L, 2L);

        when(interviewInviteServiceMock.getAvailableAssessorIds(COMPETITION_ID))
                .thenReturn(serviceSuccess(expectedAvailableAssessorIds));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessor-ids/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorIds)));

        verify(interviewInviteServiceMock, only()).getAvailableAssessorIds(COMPETITION_ID);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        int page = 5;
        int pageSize = 40;

        List<AssessorCreatedInviteResource> expectedAssessorCreatedInviteResources = newAssessorCreatedInviteResource()
                .build(2);

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(expectedAssessorCreatedInviteResources)
                .withNumber(page)
                .withTotalElements(200L)
                .withTotalPages(10)
                .withSize(pageSize)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "email"));

        when(interviewInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-created-invites/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "email,ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
    }

    @Test
    public void getCreatedInvites_defaultParameters() throws Exception {
        int page = 0;
        int pageSize = 20;

        List<AssessorCreatedInviteResource> expectedAssessorCreatedInviteResources = newAssessorCreatedInviteResource()
                .build(2);

        AssessorCreatedInvitePageResource expectedPageResource = newAssessorCreatedInvitePageResource()
                .withContent(expectedAssessorCreatedInviteResources)
                .withNumber(page)
                .withTotalElements(200L)
                .withTotalPages(10)
                .withSize(pageSize)
                .build();

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "name"));

        when(interviewInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-created-invites/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
    }

    @Test
    public void inviteUsers() throws Exception {
        List<ExistingUserStagedInviteResource> existingUserStagedInvites = newExistingUserStagedInviteResource()
                .withUserId(1L, 2L)
                .withCompetitionId(1L)
                .build(2);

        ExistingUserStagedInviteListResource existingUserStagedInviteList = newExistingUserStagedInviteListResource()
                .withInvites(existingUserStagedInvites)
                .build();

        when(interviewInviteServiceMock.inviteUsers(existingUserStagedInvites)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/invite-users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteList)))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).inviteUsers(existingUserStagedInvites);
    }

    @Test
    public void sendAllInvites() throws Exception {
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(interviewInviteServiceMock.sendAllInvites(COMPETITION_ID, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/send-all-invites/{competitionId}", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock).sendAllInvites(COMPETITION_ID, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();

        when(interviewInviteServiceMock.getAllInvitesToSend(COMPETITION_ID)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-send/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAllInvitesToSend(COMPETITION_ID);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        final long USER_ID = 12L;
        InterviewInviteResource invite = newInterviewInviteResource()
                .withCompetitionId(1L)
                .withCompetitionName("Juggling craziness")
                .withInviteHash("")
                .withStatus(InviteStatus.SENT)
                .build();

        InterviewParticipantResource interviewParticipantResource = newInterviewParticipantResource()
                .withStatus(PENDING)
                .withInvite(invite)
                .build();
        when(interviewInviteServiceMock.getAllInvitesByUser(USER_ID)).thenReturn(serviceSuccess(singletonList(interviewParticipantResource)));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-by-user/{user_id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();
        List<Long> inviteIds = asList(1L, 2L);

        when(interviewInviteServiceMock.getAllInvitesToResend(COMPETITION_ID, inviteIds)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-resend/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON)
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAllInvitesToResend(COMPETITION_ID, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(interviewInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/resend-invites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource))
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock).resendInvites(inviteIds, assessorInviteSendResource);
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        int page = 2;
        int size = 10;
        List<ParticipantStatus> status = Collections.singletonList(ACCEPTED);

        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(newAssessorInviteOverviewResource().build(2))
                .build();

        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "invite.email"));

        when(interviewInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-invitation-overview/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email")
                .param("statuses", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(interviewInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        when(interviewInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel-invite/delete-invite")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(interviewInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(interviewInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel-invite/delete-all-invites")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(interviewInviteServiceMock).deleteAllInvites(competitionId);
    }
}