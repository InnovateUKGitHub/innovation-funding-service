package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.CompetitionInviteController;
import org.innovateuk.ifs.assessment.transactional.AssessmentInviteService;
import org.innovateuk.ifs.documentation.InnovationAreaResourceDocs;
import org.innovateuk.ifs.documentation.RejectionReasonResourceDocs;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.primitives.Longs.asList;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
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
import static org.innovateuk.ifs.documentation.CompetitionInviteStatisticsResourceDocs.competitionInviteStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInviteStatisticsResourceDocs.competitionInviteStatisticsResourceFields;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentInviteControllerDocumentation extends BaseControllerMockMVCTest<CompetitionInviteController> {

    @Mock
    private AssessmentInviteService assessmentInviteServiceMock;

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(assessmentInviteServiceMock.getAllInvitesToSend(competitionId)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/competitioninvite/get-all-invites-to-send/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get invites for")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).getAllInvitesToSend(competitionId);
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(assessmentInviteServiceMock.getAllInvitesToResend(competitionId, inviteIds)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/competitioninvite/get-all-invites-to-resend/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get invites for")
                        ),
                        requestParameters(
                                parameterWithName("inviteIds")
                                        .description("Ids of invites to resend")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).getAllInvitesToResend(competitionId, inviteIds);
    }

    @Test
    public void getInviteToSend() throws Exception {
        long inviteId = 1L;
        AssessorInvitesToSendResource resource = assessorInvitesToSendResourceBuilder.build();

        when(assessmentInviteServiceMock.getInviteToSend(inviteId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competitioninvite/get-invite-to-send/{inviteId}", inviteId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("inviteId").description("Id of the invite being requested")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));
    }

    @Test
    public void getInvite() throws Exception {
        String hash = "invitehash";
        CompetitionInviteResource competitionInviteResource = competitionInviteResourceBuilder.build();

        when(assessmentInviteServiceMock.getInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        mockMvc.perform(get("/competitioninvite/get-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being requested")
                        ),
                        responseFields(competitionInviteFields)
                                .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "invitehash";
        CompetitionInviteResource competitionInviteResource = competitionInviteResourceBuilder.build();

        when(assessmentInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(competitionInviteResource));

        mockMvc.perform(post("/competitioninvite/open-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being opened")
                        ),
                        responseFields(competitionInviteFields)
                                .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "invitehash";
        UserResource user = newUserResource().build();

        login(user);

        when(assessmentInviteServiceMock.acceptInvite(hash, user)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/accept-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        )
                ));
    }

    @Test
    public void rejectInvite() throws Exception {
        String hash = "invitehash";
        CompetitionRejectionResource compRejection = competitionInviteResource;

        when(assessmentInviteServiceMock.rejectInvite(hash, compRejection.getRejectReason(), ofNullable(compRejection.getRejectComment()))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/reject-invite/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(compRejection)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        requestFields(competitionRejectionFields).andWithPrefix("rejectReason.", RejectionReasonResourceDocs.rejectionReasonResourceFields),
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being rejected")
                        )
                ));
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "invitehash";

        when(assessmentInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(get("/competitioninvite/check-existing-user/{hash}", hash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being checked")
                        )
                ));
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        String assessorFilter = "Name";

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "firstName"));

        when(assessmentInviteServiceMock.getAvailableAssessors(competitionId, pageable, assessorFilter))
                .thenReturn(serviceSuccess(availableAssessorPageResourceBuilder.build()));

        mockMvc.perform(get("/competitioninvite/get-available-assessors/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "firstName,asc")
                .param("assessorNameFilter", assessorFilter))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("size").optional()
                                        .description("Maximum number of elements in a single page. Defaults to 20."),
                                parameterWithName("page").optional()
                                        .description("Page number of the paginated data. Starts at 0. Defaults to 0."),
                                parameterWithName("sort").optional()
                                        .description("The property to sort the elements on. For example `sort=firstName,asc`. Defaults to `firstName,asc`"),
                                parameterWithName("assessorNameFilter").optional()
                                        .description("Name to filter assessors by.")
                        ),
                        responseFields(availableAssessorPageResourceFields)
                                .andWithPrefix("content[].", availableAssessorResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).getAvailableAssessors(competitionId, pageable, assessorFilter);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "name"));

        when(assessmentInviteServiceMock.getCreatedInvites(competitionId, pageable)).thenReturn(serviceSuccess(assessorCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/competitioninvite/get-created-invites/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
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

        verify(assessmentInviteServiceMock, only()).getCreatedInvites(competitionId, pageable);
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        List<ParticipantStatus> status = singletonList(PENDING);
        Optional<Boolean> compliant = of(TRUE);
        Optional<String> assessorName = of("name");

        Pageable pageable = PageRequest.of(0, 20, new Sort(ASC, "invite.name"));

        List<AssessorInviteOverviewResource> content = newAssessorInviteOverviewResource().build(2);
        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(content)
                .build();

        when(assessmentInviteServiceMock.getInvitationOverview(competitionId, pageable, status, compliant, assessorName))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/competitioninvite/get-invitation-overview/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("size", "20")
                .param("page", "0")
                .param("sort", "invite.name,asc")
                .param("statuses", "PENDING")
                .param("compliant", "1")
                .param("assessorName", assessorName.get()))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
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
                                        .description("Participant statuses to filter assessors by. Can be a single status or a combination of 'ACCEPTED', 'PENDING' or 'REJECTED'"),
                                parameterWithName("compliant").optional()
                                        .description("Flag to filter assessors by their compliance."),
                                parameterWithName("assessorName").optional()
                                        .description("Filter assessors by their name.")
                        ),
                        responseFields(assessorInviteOverviewPageResourceFields)
                                .andWithPrefix("content[].", assessorInviteOverviewResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status, compliant, assessorName);
    }

    @Test
    public void getAssessorsNotAcceptedInviteIds() throws Exception {
        long competitionId = 1L;
        List<ParticipantStatus> statuses = Arrays.asList(PENDING, REJECTED);
        Optional<Boolean> compliant = of(TRUE);
        Optional<String> assessorName = of("name");

        List<Long> expectedInviteIds = asList(1L, 2L);

        when(assessmentInviteServiceMock.getAssessorsNotAcceptedInviteIds(competitionId, statuses, compliant, assessorName))
                .thenReturn(serviceSuccess(expectedInviteIds));

        mockMvc.perform(get("/competitioninvite/get-assessors-not-accepted-invite-ids/{competitionId}", 1L)
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("statuses", "PENDING,REJECTED")
                .param("compliant", "1")
                .param("assessorName", assessorName.get()))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        requestParameters(
                                parameterWithName("statuses")
                                        .description("Participant statuses to filter assessors by. Can only be 'REJECTED', 'PENDING' or both."),
                                parameterWithName("compliant").optional()
                                        .description("Flag to filter assessors by their compliance."),
                                parameterWithName("assessorName").optional()
                                        .description("Filter assessors by their name.")
                        ),
                        responseFields(fieldWithPath("[]").description("List of invite ids of Assessors who have not accepted for a competition"))
                ));

        verify(assessmentInviteServiceMock, only()).getAssessorsNotAcceptedInviteIds(competitionId, statuses, compliant, assessorName);
    }

    @Test
    public void getInviteStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource statisticsResource = competitionInviteStatisticsResourceBuilder.build();

        when(assessmentInviteServiceMock.getInviteStatistics(competitionId)).thenReturn(serviceSuccess(statisticsResource));

        mockMvc.perform(get("/competitioninvite/get-invite-statistics/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition the invite stats are for")
                        ),
                        responseFields(competitionInviteStatisticsResourceFields)
                ));
    }

    @Test
    public void inviteUser() throws Exception {
        ExistingUserStagedInviteResource existingUserStagedInviteResource = existingUserStagedInviteResourceBuilder.build();

        when(assessmentInviteServiceMock.inviteUser(existingUserStagedInviteResource)).thenReturn(serviceSuccess(competitionInviteResourceBuilder.build()));

        mockMvc.perform(post("/competitioninvite/invite-user")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        requestFields(existingUserStagedInviteResourceFields),
                        responseFields(competitionInviteFields)
                                .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)

                ));

        verify(assessmentInviteServiceMock, only()).inviteUser(existingUserStagedInviteResource);
    }

    @Test
    public void inviteNewUser() throws Exception {
        NewUserStagedInviteResource newUserStagedInviteResource = newUserStagedInviteResourceBuilder.build();

        when(assessmentInviteServiceMock.inviteUser(newUserStagedInviteResource)).thenReturn(serviceSuccess(competitionInviteResourceBuilder.build()));

        mockMvc.perform(post("/competitioninvite/invite-new-user")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(newUserStagedInviteResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        requestFields(newUserStagedInviteResourceFields),
                        responseFields(competitionInviteFields)
                                .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).inviteUser(newUserStagedInviteResource);
    }

    @Test
    public void inviteUsers() throws Exception {
        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
        List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

        when(assessmentInviteServiceMock.inviteUsers(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/invite-users")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of existing users to be invited to assess the competition")
                        ).andWithPrefix("invites[].", existingUserStagedInviteResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).inviteUsers(existingUserStagedInviteResources);
    }

    @Test
    public void inviteNewUsers() throws Exception {
        long competitionId = 1L;

        NewUserStagedInviteListResource newUserStagedInviteListResource = newUserStagedInviteListResourceBuilder.build();
        List<NewUserStagedInviteResource> newUserStagedInviteResources = newUserStagedInviteListResource.getInvites();

        when(assessmentInviteServiceMock.inviteNewUsers(newUserStagedInviteResources, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/invite-new-users/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(newUserStagedInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to invite the users to")
                        ),
                        requestFields(
                                fieldWithPath("invites[]").description("List of new users to be invited to assess the competition")
                        ).andWithPrefix("invites[].", newUserStagedInviteResourceFields)
                ));

        verify(assessmentInviteServiceMock, only()).inviteNewUsers(newUserStagedInviteResources, competitionId);
    }

    @Test
    public void deleteInvite() throws Exception {
        String email = "firstname.lastname@email.com";
        long competitionId = 1L;

        when(assessmentInviteServiceMock.deleteInvite(email, competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/competitioninvite/delete-invite")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("email", email)
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent())
                .andDo(document("competitioninvite/{method-name}",
                        requestParameters(
                                parameterWithName("email").description("Email address of the invite"),
                                parameterWithName("competitionId").description("Id of the competition")
                        )
                ));

        verify(assessmentInviteServiceMock, only()).deleteInvite(email, competitionId);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(assessmentInviteServiceMock.deleteAllInvites(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/competitioninvite/delete-all-invites")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("competitionId", String.valueOf(competitionId)))
                .andExpect(status().isNoContent())
                .andDo(document("competitioninvite/{method-name}",
                        requestParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        )
                ));

        verify(assessmentInviteServiceMock, only()).deleteAllInvites(competitionId);
    }

    @Test
    public void sendAllInvites() throws Exception {
        long competitionId = 1L;

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(assessmentInviteServiceMock.sendAllInvites(competitionId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/send-all-invites/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to send assessor invites for")
                        ),
                        requestFields(
                                fieldWithPath("subject").description("The subject of the invitation"),
                                fieldWithPath("content").description("The custom content for this invitation")
                        )
                ));

        verify(assessmentInviteServiceMock, only()).sendAllInvites(competitionId, assessorInviteSendResource);
    }

    @Test
    public void resendInvite() throws Exception {
        long inviteId = 1L;

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(assessmentInviteServiceMock.resendInvite(inviteId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/resend-invite/{inviteId}", inviteId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        pathParameters(
                                parameterWithName("inviteId").description("Id of the invite being resent")
                        ),
                        requestFields(assessorInviteSendResourceFields)
                ));
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(assessmentInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/resend-invites")
                .header("IFS_AUTH_TOKEN", "123abc")
                .param("inviteIds", simpleJoiner(inviteIds, ","))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("competitioninvite/{method-name}",
                        requestParameters(
                                parameterWithName("inviteIds")
                                        .description("Ids of invites to resend")
                        ),
                        requestFields(assessorInviteSendResourceFields)
                ));
    }
}
