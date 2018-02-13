package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentInterviewPanelInviteResourceBuilder.newAssessmentInterviewPanelInviteResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.AssessmentInterviewPanelParticipantResourceBuilder.newAssessmentInterviewPanelParticipantResource;
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

public class AssessmentInterviewPanelInviteControllerTest extends BaseControllerMockMVCTest<AssessmentInterviewPanelInviteController> {

    private static final long COMPETITION_ID = 1L;

    @Override
    protected AssessmentInterviewPanelInviteController supplyControllerUnderTest() {
        return new AssessmentInterviewPanelInviteController();
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

        when(assessmentInterviewPanelInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessors/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "lastName,desc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
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

        when(assessmentInterviewPanelInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessors/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
    }

    @Test
    public void getAvailableAssessorsIds() throws Exception {
        List<Long> expectedAvailableAssessorIds = asList(1L, 2L);

        when(assessmentInterviewPanelInviteServiceMock.getAvailableAssessorIds(COMPETITION_ID))
                .thenReturn(serviceSuccess(expectedAvailableAssessorIds));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessor-ids/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorIds)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getAvailableAssessorIds(COMPETITION_ID);
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

        when(assessmentInterviewPanelInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-created-invites/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "email,ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
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

        when(assessmentInterviewPanelInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-created-invites/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
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

        when(assessmentInterviewPanelInviteServiceMock.inviteUsers(existingUserStagedInvites)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/invite-users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteList)))
                .andExpect(status().isOk());

        verify(assessmentInterviewPanelInviteServiceMock, only()).inviteUsers(existingUserStagedInvites);
    }

    @Test
    public void sendAllInvites() throws Exception {
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(assessmentInterviewPanelInviteServiceMock.sendAllInvites(COMPETITION_ID, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/send-all-invites/{competitionId}", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(assessmentInterviewPanelInviteServiceMock).sendAllInvites(COMPETITION_ID, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();

        when(assessmentInterviewPanelInviteServiceMock.getAllInvitesToSend(COMPETITION_ID)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-send/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assessmentInterviewPanelInviteServiceMock, only()).getAllInvitesToSend(COMPETITION_ID);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        final long USER_ID = 12L;
        AssessmentInterviewPanelInviteResource invite = newAssessmentInterviewPanelInviteResource()
                .withCompetitionId(1L)
                .withCompetitionName("Juggling craziness")
                .withInviteHash("")
                .withStatus(InviteStatus.SENT)
                .build();

        AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withStatus(PENDING)
                .withInvite(invite)
                .build();
        when(assessmentInterviewPanelInviteServiceMock.getAllInvitesByUser(USER_ID)).thenReturn(serviceSuccess(singletonList(assessmentInterviewPanelParticipantResource)));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-by-user/{user_id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();
        List<Long> inviteIds = asList(1L, 2L);

        when(assessmentInterviewPanelInviteServiceMock.getAllInvitesToResend(COMPETITION_ID, inviteIds)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-resend/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON)
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(assessmentInterviewPanelInviteServiceMock, only()).getAllInvitesToResend(COMPETITION_ID, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(assessmentInterviewPanelInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/resend-invites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource))
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(assessmentInterviewPanelInviteServiceMock).resendInvites(inviteIds, assessorInviteSendResource);
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

        when(assessmentInterviewPanelInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-invitation-overview/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email")
                .param("statuses", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentInterviewPanelInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        when(assessmentInterviewPanelInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel-invite/delete-invite")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(assessmentInterviewPanelInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(assessmentInterviewPanelInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel-invite/delete-all-invites")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(assessmentInterviewPanelInviteServiceMock).deleteAllInvites(competitionId);
    }
}