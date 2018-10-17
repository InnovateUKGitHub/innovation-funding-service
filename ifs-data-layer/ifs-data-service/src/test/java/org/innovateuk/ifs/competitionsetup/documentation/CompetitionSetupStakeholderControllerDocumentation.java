package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupStakeholderController;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.documentation.CompetitionInviteDocs;
import org.innovateuk.ifs.documentation.InviteUserResourceDocs;
import org.innovateuk.ifs.documentation.UserRegistrationResourceDocs;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderInviteResourceBuilder.newStakeholderInviteResource;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderRegistrationResourceBuilder.newStakeholderRegistrationResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupStakeholderControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupStakeholderController> {

    private InviteUserResource inviteUserResource;

    @Mock
    private CompetitionSetupStakeholderService competitionSetupStakeholderService;

    @Mock
    private RegistrationService registrationService;

    private final String TEST_HASH = "hash1234";

    @Override
    protected CompetitionSetupStakeholderController supplyControllerUnderTest() {
        return new CompetitionSetupStakeholderController();
    }

    @Before
    public void setUp() {
        UserResource invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("Rayon")
                .withLastName("Kevin")
                .withEmail("Rayon.Kevin@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser);
    }

    @Test
    public void inviteStakeholder() throws Exception {

        long competitionId = 1L;

        when(competitionSetupStakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId)).thenReturn(serviceSuccess());


        mockMvc.perform(post("/competition/setup/{competitionId}/stakeholder/invite", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk())
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the Competition to which the Stakeholder is being invited")
                        ),
                        requestFields(InviteUserResourceDocs.inviteUserResourceFields)
                ));

        verify(competitionSetupStakeholderService).inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId);
    }

    @Test
    public void findStakeholders() throws Exception {

        long competitionId = 1L;

        List<UserResource> stakeholderUsers = UserResourceBuilder.newUserResource().build(2);

        when(competitionSetupStakeholderService.findStakeholders(competitionId)).thenReturn(serviceSuccess(stakeholderUsers));

        mockMvc.perform(get("/competition/setup/{competitionId}/stakeholder/find-all", competitionId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(stakeholderUsers)))
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("The competition id for which stakeholders need to be retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of stakeholders assigned to the competition")
                        )
                ));

        verify(competitionSetupStakeholderService).findStakeholders(competitionId);
    }

    @Test
    public void getInvite() throws Exception {
        StakeholderInviteResource invite = newStakeholderInviteResource()
                .withHash(TEST_HASH)
                .withCompetition(1L)
                .withEmail("test@test.test")
                .build();

        when(competitionSetupStakeholderService.getInviteByHash(TEST_HASH)).thenReturn(serviceSuccess(invite));

        mockMvc.perform(get("/competition/setup/get-invite/{inviteHash}", TEST_HASH))
                .andExpect(status().isOk())
                .andDo(document("competition/setup/{method-name}",
                                pathParameters(
                                        parameterWithName("inviteHash").description("Hash of the invite being requested")
                                ),
                                responseFields(CompetitionInviteDocs.stakeholderInviteResourceFields)
                ));
    }

    @Test
    public void createStakeholder() throws Exception {

        StakeholderRegistrationResource resource = newStakeholderRegistrationResource()
                .withFirstName("John")
                .withLastName("Smith")
                .withPassword("superSecurePassword")
                .build();

        when(registrationService.createStakeholder(TEST_HASH, resource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/stakeholder/create/{inviteHash}", TEST_HASH)
                                .contentType(APPLICATION_JSON)
                                .content(toJson(resource)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("competition/setup/stakeholder/{method-name}",
                                pathParameters(
                                        parameterWithName("inviteHash").description("Hash of the invite to create a user for")
                                ),
                                requestFields(UserRegistrationResourceDocs.stakeholderRegistrationResourceFields)
                                ));
    }

    @Test
    public void addStakeholder() throws Exception {

        long competitionId = 1L;
        long stakeholderUserId = 2L;

        when(competitionSetupStakeholderService.addStakeholder(competitionId, stakeholderUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{competitionId}/stakeholder/{stakeholderUserId}/add", competitionId, stakeholderUserId)
                )
                .andExpect(status().isOk())
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the Competition to which the Stakeholder is being added"),
                                parameterWithName("stakeholderUserId").description("User id of the Stakeholder which is being added")
                        )
                ));

        verify(competitionSetupStakeholderService).addStakeholder(competitionId, stakeholderUserId);
    }

    @Test
    public void removeStakeholder() throws Exception {

        long competitionId = 1L;
        long stakeholderUserId = 2L;

        when(competitionSetupStakeholderService.removeStakeholder(competitionId, stakeholderUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{competitionId}/stakeholder/{stakeholderUserId}/remove", competitionId, stakeholderUserId)
        )
                .andExpect(status().isOk())
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the Competition from which the Stakeholder is being removed"),
                                parameterWithName("stakeholderUserId").description("User id of the Stakeholder which is being removed")
                        )
                ));

        verify(competitionSetupStakeholderService).removeStakeholder(competitionId, stakeholderUserId);
    }

    @Test
    public void findPendingStakeholderInvites() throws Exception {

        long competitionId = 1L;

        List<UserResource> pendingStakeholderInvites = UserResourceBuilder.newUserResource().build(2);

        when(competitionSetupStakeholderService.findPendingStakeholderInvites(competitionId)).thenReturn(serviceSuccess(pendingStakeholderInvites));

        mockMvc.perform(get("/competition/setup/{competitionId}/stakeholder/pending-invites", competitionId)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingStakeholderInvites)))
                .andDo(document("competition/setup/stakeholder/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("The competition id for which pending stakeholder invites need to be retrieved")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of pending stakeholder invites for the competition")
                        )
                ));

        verify(competitionSetupStakeholderService).findPendingStakeholderInvites(competitionId);
    }
}


