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
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.AssessmentPanelParticipantResourceBuilder.newAssessmentPanelParticipantResource;
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
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.*;
import static org.innovateuk.ifs.user.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelInviteControllerTest extends BaseControllerMockMVCTest<AssessmentPanelInviteController> {

    private static final long COMPETITION_ID = 1L;

    @Override
    protected AssessmentPanelInviteController supplyControllerUnderTest() {
        return new AssessmentPanelInviteController();
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

        when(assessmentPanelInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessors/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "lastName,desc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
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

        when(assessmentPanelInviteServiceMock.getAvailableAssessors(COMPETITION_ID, pageable))
                .thenReturn(serviceSuccess(expectedAvailableAssessorPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessors/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorPageResource)));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessors(COMPETITION_ID, pageable);
    }

    @Test
    public void getAvailableAssessorsIds() throws Exception {
        List<Long> expectedAvailableAssessorIds = asList(1L, 2L);

        when(assessmentPanelInviteServiceMock.getAvailableAssessorIds(COMPETITION_ID))
                .thenReturn(serviceSuccess(expectedAvailableAssessorIds));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessorIds/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedAvailableAssessorIds)));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessorIds(COMPETITION_ID);
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

        when(assessmentPanelInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getCreatedInvites/{competitionId}", COMPETITION_ID)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(pageSize))
                .param("sort", "email,ASC"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentPanelInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
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

        when(assessmentPanelInviteServiceMock.getCreatedInvites(COMPETITION_ID, pageable)).thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getCreatedInvites/{competitionId}", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentPanelInviteServiceMock, only()).getCreatedInvites(COMPETITION_ID, pageable);
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

        when(assessmentPanelInviteServiceMock.inviteUsers(existingUserStagedInvites)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/inviteUsers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteList)))
                .andExpect(status().isOk());

        verify(assessmentPanelInviteServiceMock, only()).inviteUsers(existingUserStagedInvites);
    }

    @Test
    public void sendAllInvites() throws Exception {
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(assessmentPanelInviteServiceMock.sendAllInvites(COMPETITION_ID, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/sendAllInvites/{competitionId}", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(assessmentPanelInviteServiceMock).sendAllInvites(COMPETITION_ID, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();

        when(assessmentPanelInviteServiceMock.getAllInvitesToSend(COMPETITION_ID)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/assessmentpanelinvite/getAllInvitesToSend/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assessmentPanelInviteServiceMock, only()).getAllInvitesToSend(COMPETITION_ID);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        final long USER_ID = 12L;
        AssessmentPanelInviteResource invite = newAssessmentPanelInviteResource()
                .withCompetitionId(1L)
                .withCompetitionName("Juggling craziness")
                .withInviteHash("")
                .withStatus(InviteStatus.SENT)
                .build();

        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withStatus(PENDING)
                .withInvite(invite)
                .build();
        when(assessmentPanelInviteServiceMock.getAllInvitesByUser(USER_ID)).thenReturn(serviceSuccess(singletonList(assessmentPanelParticipantResource)));

        mockMvc.perform(get("/assessmentpanelinvite/getAllInvitesByUser/{user_id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        AssessorInvitesToSendResource resource = newAssessorInvitesToSendResource().build();
        List<Long> inviteIds = asList(1L, 2L);

        when(assessmentPanelInviteServiceMock.getAllInvitesToResend(COMPETITION_ID, inviteIds)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/assessmentpanelinvite/getAllInvitesToResend/{competitionId}", COMPETITION_ID).contentType(MediaType.APPLICATION_JSON)
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(assessmentPanelInviteServiceMock, only()).getAllInvitesToResend(COMPETITION_ID, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        when(assessmentPanelInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/resendInvites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource))
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk());

        verify(assessmentPanelInviteServiceMock).resendInvites(inviteIds, assessorInviteSendResource);
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

        when(assessmentPanelInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getInvitationOverview/{competitionId}", competitionId)
                .param("page", "2")
                .param("size", "10")
                .param("sort", "invite.email")
                .param("statuses", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedPageResource)));

        verify(assessmentPanelInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        when(assessmentPanelInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/assessmentpanelinvite/deleteInvite")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(assessmentPanelInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(assessmentPanelInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/assessmentpanelinvite/deleteAllInvites")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(assessmentPanelInviteServiceMock).deleteAllInvites(competitionId);
    }
}