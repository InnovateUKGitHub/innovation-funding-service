package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessmentPanelInviteController;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentPanelInviteDocs.assessmentPanelInviteFields;
import static org.innovateuk.ifs.documentation.AssessmentPanelInviteDocs.assessmentPanelInviteResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorCreatedInvitePageResourceDocs.assessorCreatedInvitePageResourceFields;
import static org.innovateuk.ifs.documentation.AssessorCreatedInviteResourceDocs.assessorCreatedInviteResourceFields;
import static org.innovateuk.ifs.documentation.AssessorInviteOverviewPageResourceDocs.assessorInviteOverviewPageResourceFields;
import static org.innovateuk.ifs.documentation.AssessorInviteOverviewResourceDocs.assessorInviteOverviewResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceBuilder;
import static org.innovateuk.ifs.documentation.AvailableAssessorPageResourceDocs.availableAssessorPageResourceFields;
import static org.innovateuk.ifs.documentation.AvailableAssessorResourceDocs.availableAssessorResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInviteDocs.*;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelInviteControllerDocumentation extends BaseControllerMockMVCTest<AssessmentPanelInviteController> {

    @Override
    protected AssessmentPanelInviteController supplyControllerUnderTest() {
        return new AssessmentPanelInviteController();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "firstName"));

        when(assessmentPanelInviteServiceMock.getAvailableAssessors(competitionId, pageable))
                .thenReturn(serviceSuccess(availableAssessorPageResourceBuilder.build()));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessors/{competitionId}", competitionId)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
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

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessors(competitionId, pageable);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        when(assessmentPanelInviteServiceMock.getAvailableAssessorIds(competitionId))
                .thenReturn(serviceSuccess(asList(1L, 2L)));

        mockMvc.perform(get("/assessmentpanelinvite/getAvailableAssessorIds/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition")
                        ),
                        responseFields(fieldWithPath("[]").description("List of available assessor ids "))
                ));

        verify(assessmentPanelInviteServiceMock, only()).getAvailableAssessorIds(competitionId);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        when(assessmentPanelInviteServiceMock.getCreatedInvites(competitionId, pageable)).thenReturn(serviceSuccess(assessorCreatedInvitePageResourceBuilder.build()));

        mockMvc.perform(get("/assessmentpanelinvite/getCreatedInvites/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
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

        verify(assessmentPanelInviteServiceMock, only()).getCreatedInvites(competitionId, pageable);
    }

    @Test
    public void inviteUsers() throws Exception {
        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = existingUserStagedInviteListResourceBuilder.build();
        List<ExistingUserStagedInviteResource> existingUserStagedInviteResources = existingUserStagedInviteListResource.getInvites();

        when(assessmentPanelInviteServiceMock.inviteUsers(existingUserStagedInviteResources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/inviteUsers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(existingUserStagedInviteListResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        requestFields(
                                fieldWithPath("invites[]").description("List of existing users to be invited to the assessment panel")
                        ).andWithPrefix("invites[].", existingUserStagedInviteResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).inviteUsers(existingUserStagedInviteResources);
    }

    @Test
    public void sendAllInvites() throws Exception {
        long competitionId = 2L;

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(assessmentPanelInviteServiceMock.sendAllInvites(competitionId, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/sendAllInvites/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to send assessor panel invites for")
                        ),
                        requestFields(
                                fieldWithPath("subject").description("The subject of the invitation"),
                                fieldWithPath("content").description("The custom content for this invitation")
                        )
                ));

        verify(assessmentPanelInviteServiceMock, only()).sendAllInvites(competitionId, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(assessmentPanelInviteServiceMock.getAllInvitesToSend(competitionId)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/assessmentpanelinvite/getAllInvitesToSend/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get assemment panel invites for")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).getAllInvitesToSend(competitionId);
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInvitesToSendResource assessorInvitesToSendResource = assessorInvitesToSendResourceBuilder.build();

        when(assessmentPanelInviteServiceMock.getAllInvitesToResend(competitionId, inviteIds)).thenReturn(serviceSuccess(assessorInvitesToSendResource));

        mockMvc.perform(get("/assessmentpanelinvite/getAllInvitesToResend/{competitionId}", competitionId)
                .param("inviteIds", simpleJoiner(inviteIds, ",")))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition to get invites for")
                        ),
                        requestParameters(
                                parameterWithName("inviteIds")
                                        .description("Ids of invites to resend")
                        ),
                        responseFields(assessorInvitesToSendResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).getAllInvitesToResend(competitionId, inviteIds);
    }

    @Test
    public void resendInvites() throws Exception {
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInviteSendResource assessorInviteSendResource = assessorInviteSendResourceBuilder.build();
        when(assessmentPanelInviteServiceMock.resendInvites(inviteIds, assessorInviteSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/resendInvites")
                .param("inviteIds", simpleJoiner(inviteIds, ","))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessorInviteSendResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
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

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "invite.name"));

        List<AssessorInviteOverviewResource> content = newAssessorInviteOverviewResource().build(2);
        AssessorInviteOverviewPageResource expectedPageResource = newAssessorInviteOverviewPageResource()
                .withContent(content)
                .build();

        when(assessmentPanelInviteServiceMock.getInvitationOverview(competitionId, pageable, status))
                .thenReturn(serviceSuccess(expectedPageResource));

        mockMvc.perform(get("/assessmentpanelinvite/getInvitationOverview/{competitionId}", 1L)
                .param("size", "20")
                .param("page", "0")
                .param("sort", "invite.name,asc")
                .param("statuses", "PENDING"))
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
                                        .description("Participant statuses to filter assessors by. Can be a single status or a combination of 'ACCEPTED', 'PENDING' or 'REJECTED'")
                        ),
                        responseFields(assessorInviteOverviewPageResourceFields)
                                .andWithPrefix("content[].", assessorInviteOverviewResourceFields)
                ));

        verify(assessmentPanelInviteServiceMock, only()).getInvitationOverview(competitionId, pageable, status);
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "invitehash";
        AssessmentPanelInviteResource assessmentPanelInviteResource = assessmentPanelInviteResourceBuilder.build();

        when(assessmentPanelInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(assessmentPanelInviteResource));

        mockMvc.perform(post("/assessmentpanelinvite/openInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being opened")
                        ),
                        responseFields(assessmentPanelInviteFields)
                ));
    }

    @Test
    public void acceptInvite() throws Exception {
        String hash = "invitehash";
        UserResource user = newUserResource().build();

        login(user);

        when(assessmentPanelInviteServiceMock.acceptInvite(hash, user)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/acceptInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        )
                ));
    }

    @Test
    public void rejectInvite() throws Exception {
        String hash = "invitehash";

        when(assessmentPanelInviteServiceMock.rejectInvite(hash)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanelinvite/rejectInvite/{hash}", hash))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being rejected")
                        )
                ));
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "invitehash";

        when(assessmentPanelInviteServiceMock.checkExistingUser(hash)).thenReturn(serviceSuccess(TRUE));

        mockMvc.perform(get("/assessmentpanelinvite/checkExistingUser/{hash}", hash))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("assessmentpanelinvite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being checked")
                        )
                ));
    }
}
