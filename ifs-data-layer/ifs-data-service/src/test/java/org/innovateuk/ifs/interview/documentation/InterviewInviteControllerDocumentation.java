package org.innovateuk.ifs.interview.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.InterviewParticipantResourceDocs;
import org.innovateuk.ifs.interview.controller.InterviewInviteController;
import org.innovateuk.ifs.interview.transactional.InterviewInviteService;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.AssessorCreatedInviteResourceDocs.assessorCreatedInviteResourceFields;
import static org.innovateuk.ifs.documentation.AssessorInviteOverviewPageResourceDocs.assessorInviteOverviewPageResourceFields;
import static org.innovateuk.ifs.documentation.AssessorInviteOverviewResourceDocs.assessorInviteOverviewResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceBuilder;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorResourceDocs.availableAssessorResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.*;
import static org.innovateuk.ifs.documentation.InterviewInviteDocs.INTERVIEW_INVITE_RESOURCE_BUILDER;
import static org.innovateuk.ifs.documentation.InterviewInviteDocs.interviewInviteFields;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantResourceBuilder.newInterviewParticipantResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InterviewInviteControllerDocumentation extends BaseControllerMockMVCTest<InterviewInviteController> {

    @Override
    protected InterviewInviteController supplyControllerUnderTest() {
        return new InterviewInviteController();
    }


    @Mock
    private InterviewInviteService interviewInviteServiceMock;

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "firstName"));

        when(interviewInviteServiceMock.getAvailableAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(availableAssessorPageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessors/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "firstName,asc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAvailableAssessors(competitionId, pageable);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        when(interviewInviteServiceMock.getAvailableAssessorIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/interview-panel-invite/get-available-assessor-ids/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAvailableAssessorIds(competitionId);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "name"));

        when(interviewInviteServiceMock.getCreatedInvites(competitionId, pageable)).thenReturn(serviceSuccess(assessorCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/interview-panel-invite/get-created-invites/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getCreatedInvites(competitionId, pageable);
    }

    @Test
    public void inviteUsers() throws Exception {
        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
        List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

        when(interviewInviteServiceMock.inviteUsers(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/invite-users")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource)))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).inviteUsers(existingUserStagedInviteResources);
    }

    @Test
    public void sendAllInvites() throws Exception {
        long competitionId = 2L;

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(interviewInviteServiceMock.sendAllInvites(competitionId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/send-all-invites/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(assessorInviteSendResource)))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).sendAllInvites(competitionId, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(interviewInviteServiceMock.getAllInvitesToSend(competitionId)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-send/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAllInvitesToSend(competitionId);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        final long userId = 12L;
        InterviewParticipantResource interviewPanelParticipantResource = newInterviewParticipantResource().build();
        when(interviewInviteServiceMock.getAllInvitesByUser(userId)).thenReturn(serviceSuccess(singletonList(interviewPanelParticipantResource)));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-by-user/{userId}", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(interviewInviteServiceMock.getAllInvitesToResend(competitionId, inviteIds)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/interview-panel-invite/get-all-invites-to-resend/{competitionId}", competitionId)
                .param("inviteIds", simpleJoiner(inviteIds, ","))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getAllInvitesToResend(competitionId, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(interviewInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/resend-invites")
                .param("inviteIds", simpleJoiner(inviteIds, ","))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        List<ParticipantStatus> status = singletonList(PENDING);

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "invite.name"));

        List<AssessorInviteOverviewResource> content = newAssessorInviteOverviewResource().build(2);
        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(content)
                .build();

        when(interviewInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/interview-panel-invite/get-invitation-overview/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "invite.name,asc")
                .param("statuses", "PENDING")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "invitehash";
        InterviewInviteResource interviewReviewPanelInviteResource = INTERVIEW_INVITE_RESOURCE_BUILDER.build();

        when(interviewInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(interviewReviewPanelInviteResource));

        mockMvc.perform(post("/interview-panel-invite/open-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "invitehash";

        when(interviewInviteServiceMock.acceptInvite(hash)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/accept-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void rejectInvite() throws Exception {
        String hash = "invitehash";

        when(interviewInviteServiceMock.rejectInvite(hash)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/interview-panel-invite/reject-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "invitehash";

        when(interviewInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(get("/interview-panel-invite/check-existing-user/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void getNonAcceptedAssessorInviteIds() throws Exception {
        long competitionId = 1L;

        when(interviewInviteServiceMock.getNonAcceptedAssessorInviteIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/interview-panel-invite/get-non-accepted-assessor-invite-ids/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(interviewInviteServiceMock, only()).getNonAcceptedAssessorInviteIds(competitionId);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@email.com";
        long competitionId = 1L;

        when(interviewInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/interview-panel-invite/delete-invite")
                .header("IFS_AUTH_TOKEN", "123abc")
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
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent());

        verify(interviewInviteServiceMock, only()).deleteAllInvites(competitionId);
    }
}
