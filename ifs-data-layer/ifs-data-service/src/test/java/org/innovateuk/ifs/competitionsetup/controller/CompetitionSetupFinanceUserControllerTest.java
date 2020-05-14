package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceUserService;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CompetitionSetupFinanceUserControllerTest extends BaseControllerMockMVCTest<CompetitionSetupFinanceUserController> {

    private InviteUserResource inviteUserResource;
    private final String TEST_HASH = "hash8888";

    @Mock
    private CompetitionSetupFinanceUserService competitionSetupFinanceUserService;

    @Mock
    private RegistrationService registrationService;

    @Override
    protected CompetitionSetupFinanceUserController supplyControllerUnderTest() {
        return new CompetitionSetupFinanceUserController();
    }

    @Before
    public void setUp() {
        UserResource invitedUser = newUserResource()
                .withFirstName("Erin")
                .withLastName("Jones")
                .withEmail("erin.jones@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser);
    }

    @Test
    public void inviteCompetitionFinanceUser() throws Exception {

        long competitionId = 1L;

        when(competitionSetupFinanceUserService.inviteFinanceUser(inviteUserResource.getInvitedUser(), competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{competitionId}/finance-users/invite", competitionId)
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(competitionSetupFinanceUserService).inviteFinanceUser(inviteUserResource.getInvitedUser(), competitionId);
    }

    @Test
    public void findCompetitionFinanceUser() throws Exception {

        long competitionId = 1L;
        List<UserResource> competitionFinanceUsers = newUserResource().withRoleGlobal(Role.EXTERNAL_FINANCE).build(2);

        when(competitionSetupFinanceUserService.findFinanceUser(competitionId)).thenReturn(serviceSuccess(competitionFinanceUsers));

        mockMvc.perform(get("/competition/setup/{competitionId}/finance-users/find-all", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(competitionFinanceUsers)));

        verify(competitionSetupFinanceUserService).findFinanceUser(competitionId);
    }

    @Test
    public void getInvite() throws Exception {

        CompetitionFinanceInviteResource invite = new CompetitionFinanceInviteResource();
        invite.setHash(TEST_HASH);

        when(competitionSetupFinanceUserService.getInviteByHash(TEST_HASH)).thenReturn(serviceSuccess(invite));

        mockMvc.perform(get("/competition/setup/get-finance-users-invite/" + TEST_HASH))
                .andExpect(status().isOk());

        verify(competitionSetupFinanceUserService).getInviteByHash(TEST_HASH);
    }

    @Test
    public void createCompetitionFinanceUser() throws Exception {

        CompetitionFinanceRegistrationResource resource = new CompetitionFinanceRegistrationResource();
        resource.setFirstName("Leny");
        resource.setLastName("White");

        when(registrationService.createCompetitionFinanceUser(TEST_HASH, resource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/finance-users/create/" + TEST_HASH)
                .contentType(APPLICATION_JSON)
                .content(toJson(resource)))
                .andExpect(status().is2xxSuccessful());

        verify(registrationService).createCompetitionFinanceUser(TEST_HASH, resource);

    }

    @Test
    public void addFinanceUser() throws Exception {

        long competitionId = 1L;
        long competitionFinanceUserId = 2L;

        when(competitionSetupFinanceUserService.addFinanceUser(competitionId, competitionFinanceUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{competitionId}/finance-users/{userId}/add", competitionId, competitionFinanceUserId))
                .andExpect(status().isOk());

        verify(competitionSetupFinanceUserService).addFinanceUser(competitionId, competitionFinanceUserId);
    }

    @Test
    public void removeCompetitionFinanceUser() throws Exception {

        long competitionId = 1L;
        long competitionFinanceUserId = 2L;

        when(competitionSetupFinanceUserService.removeFinanceUser(competitionId, competitionFinanceUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/setup/{competitionId}/finance-users/{userId}/remove", competitionId, competitionFinanceUserId))
                .andExpect(status().isOk());

        verify(competitionSetupFinanceUserService).removeFinanceUser(competitionId, competitionFinanceUserId);
    }

    @Test
    public void findPendingFinanceUseInvites() throws Exception {

        long competitionId = 1L;
        List<UserResource> pendingCompetitionFinanceInvites = newUserResource().withRoleGlobal(Role.EXTERNAL_FINANCE).build(2);

        when(competitionSetupFinanceUserService.findPendingFinanceUseInvites(competitionId)).thenReturn(serviceSuccess(pendingCompetitionFinanceInvites));

        mockMvc.perform(get("/competition/setup/{competitionId}/finance-users/pending-invites", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingCompetitionFinanceInvites)));

        verify(competitionSetupFinanceUserService).findPendingFinanceUseInvites(competitionId);
    }

}