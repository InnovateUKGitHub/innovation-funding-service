package org.innovateuk.ifs.review.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.ReviewParticipantResourceDocs;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.review.controller.ReviewInviteController;
import org.innovateuk.ifs.review.transactional.ReviewInviteService;
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
import static org.innovateuk.ifs.documentation.ReviewInviteDocs.REVIEW_INVITE_RESOURCE_BUILDER;
import static org.innovateuk.ifs.documentation.ReviewInviteDocs.reviewInviteFields;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.review.builder.ReviewParticipantResourceBuilder.newReviewParticipantResource;
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

public class ReviewInviteControllerDocumentation extends BaseControllerMockMVCTest<ReviewInviteController> {

    @Mock
    private ReviewInviteService reviewInviteServiceMock;

    @Override
    protected ReviewInviteController supplyControllerUnderTest() {
        return new ReviewInviteController();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "firstName"));

        when(reviewInviteServiceMock.getAvailableAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(availableAssessorPageResourceBuilder.build()));

        mockMvc.perform(get("/assessment-panel-invite/get-available-assessors/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=firstName,asc`. Defaults to `firstName,asc`")
                        ),
                        responseFields(availableAssessorPageResourceFields)
                                .andWithPrefix("content[].", availableAssessorResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).getAvailableAssessors(competitionId, pageable);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        when(reviewInviteServiceMock.getAvailableAssessorIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/assessment-panel-invite/get-available-assessor-ids/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of available assessor ids "))
                ));

        verify(reviewInviteServiceMock, only()).getAvailableAssessorIds(competitionId);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        when(reviewInviteServiceMock.getCreatedInvites(competitionId, pageable)).thenReturn(serviceSuccess(assessorCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/assessment-panel-invite/get-created-invites/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
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
                        responseFields(assessorCreatedInvitePageResourceFields)
                                .andWithPrefix("content[]", assessorCreatedInviteResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).getCreatedInvites(competitionId, pageable);
    }

    @Test
    public void inviteUsers() throws Exception {
        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
        List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

        when(reviewInviteServiceMock.inviteUsers(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-panel-invite/invite-users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of existing users to be invited to the assessment panel")
                        ).andWithPrefix("invites[].", existingUserStagedInviteResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).inviteUsers(existingUserStagedInviteResources);
    }

    @Test
    public void sendAllInvites() throws Exception {
        long competitionId = 2L;

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(reviewInviteServiceMock.sendAllInvites(competitionId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-panel-invite/send-all-invites/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(assessorInviteSendResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to send assessor panel invites for")
                        ),
                        requestFields(
                                fieldWithPath("subject").description("The subject of the invitation"),
                                fieldWithPath("content").description("The custom content for this invitation")
                        )
                ));

        verify(reviewInviteServiceMock, only()).sendAllInvites(competitionId, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(reviewInviteServiceMock.getAllInvitesToSend(competitionId)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/assessment-panel-invite/get-all-invites-to-send/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get assemment panel invites for")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).getAllInvitesToSend(competitionId);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        final long userId = 12L;
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource().build();
        when(reviewInviteServiceMock.getAllInvitesByUser(userId)).thenReturn(serviceSuccess(singletonList(reviewParticipantResource)));

        mockMvc.perform(get("/assessment-panel-invite/get-all-invites-by-user/{userId}", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("ID of the user to get assessment panel invites for")
                        ),
                        responseFields(fieldWithPath("[]").description("List of assessment panel invites belonging to the user")
                        ).andWithPrefix("[].", ReviewParticipantResourceDocs.reviewParticipantFields)
                ));
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(reviewInviteServiceMock.getAllInvitesToResend(competitionId, inviteIds)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/assessment-panel-invite/get-all-invites-to-resend/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get invites for")
                        ),
                        requestParameters(
                                parameterWithName("inviteIds")
                                        .description("Ids of invites to resend")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).getAllInvitesToResend(competitionId, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(reviewInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-panel-invite/resend-invites")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("inviteIds", simpleJoiner(inviteIds, ","))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        requestParameters(
                                parameterWithName("inviteIds")
                                        .description("Ids of invites to resend")
                        ),
                        requestFields(assessorInviteSendResourceFields)
                ));
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        List<ParticipantStatus> status = singletonList(PENDING);

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "invite.name"));

        List<AssessorInviteOverviewResource> content = newAssessorInviteOverviewResource().build(2);
        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(content)
                .build();

        when(reviewInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/assessment-panel-invite/get-invitation-overview/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "invite.name,asc")
                .param("statuses", "PENDING")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=invite.name,asc`. Defaults to `invite.name,asc`"),
                                parameterWithName("statuses")
                                        .description("Participant statuses to filter assessors by. Can be a single status or a combination of 'ACCEPTED', 'PENDING' or 'REJECTED'")
                        ),
                        responseFields(assessorInviteOverviewPageResourceFields)
                                .andWithPrefix("content[].", assessorInviteOverviewResourceFields)
                ));

        verify(reviewInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "invitehash";
        ReviewInviteResource reviewInviteResource = REVIEW_INVITE_RESOURCE_BUILDER.build();

        when(reviewInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(reviewInviteResource));

        mockMvc.perform(post("/assessment-panel-invite/open-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being opened")
                        ),
                        responseFields(reviewInviteFields)
                ));
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "invitehash";

        when(reviewInviteServiceMock.acceptInvite(hash)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-panel-invite/accept-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        )
                ));
    }

    @Test
    public void rejectInvite() throws Exception {
        String hash = "invitehash";

        when(reviewInviteServiceMock.rejectInvite(hash)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-panel-invite/reject-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being rejected")
                        )
                ));
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "invitehash";

        when(reviewInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(get("/assessment-panel-invite/check-existing-user/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being checked")
                        )
                ));
    }

    @Test
    public void getNonAcceptedAssessorInviteIds() throws Exception {
        long competitionId = 1L;

        when(reviewInviteServiceMock.getNonAcceptedAssessorInviteIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/assessment-panel-invite/get-non-accepted-assessor-invite-ids/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("assessment-panel-invite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of non accepted assessor invite ids "))
                ));

        verify(reviewInviteServiceMock, only()).getNonAcceptedAssessorInviteIds(competitionId);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@email.com";
        long competitionId = 1L;

        when(reviewInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/assessment-panel-invite/delete-invite")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent())
                .andDo(document("assessment-panel-invite/{method-name}",
                        requestParameters(
                                parameterWithName("email").description("Email address of the invite"),
                                parameterWithName("competitionId").description("Id of the competition")
                        )
                ));

        verify(reviewInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(reviewInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/assessment-panel-invite/delete-all-invites")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent())
                .andDo(document("assessment-panel-invite/{method-name}",
                        requestParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        )
                ));

        verify(reviewInviteServiceMock, only()).deleteAllInvites(competitionId);
    }
}
