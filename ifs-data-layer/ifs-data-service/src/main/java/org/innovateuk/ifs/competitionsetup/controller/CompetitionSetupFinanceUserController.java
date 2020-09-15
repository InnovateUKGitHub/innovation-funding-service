package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceUserService;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.registration.resource.CompetitionFinanceRegistrationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;

@RestController
@RequestMapping("/competition/setup")
public class CompetitionSetupFinanceUserController {

    @Autowired
    private CompetitionSetupFinanceUserService competitionSetupFinanceUserService;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/{competitionId}/finance-users/invite")
    public RestResult<Void> inviteCompetitionFinanceUser(@PathVariable("competitionId") final long competitionId, @RequestBody InviteUserResource inviteUserResource) {
        return competitionSetupFinanceUserService.inviteFinanceUser(inviteUserResource.getInvitedUser(), competitionId).toPostResponse();
    }

    @GetMapping("/{competitionId}/finance-users/find-all")
    public RestResult<List<UserResource>> findCompetitionFinanceUser(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupFinanceUserService.findFinanceUser(competitionId).toGetResponse();
    }

    @GetMapping("/get-finance-users-invite/{inviteHash}")
    public RestResult<CompetitionFinanceInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash) {
        return competitionSetupFinanceUserService.getInviteByHash(inviteHash).toGetResponse();
    }

    @PostMapping("/finance-users/create/{inviteHash}")
    public RestResult<Void> createCompetitionFinanceUser(@PathVariable("inviteHash") String inviteHash, @RequestBody CompetitionFinanceRegistrationResource competitionFinanceRegistrationResource) {
        return registrationService.createUser(anUserCreationResource()
                .withFirstName(competitionFinanceRegistrationResource.getFirstName())
                .withLastName(competitionFinanceRegistrationResource.getLastName())
                .withPassword(competitionFinanceRegistrationResource.getPassword())
                .withInviteHash(inviteHash)
                .withRole(Role.EXTERNAL_FINANCE)
                .build())
                .andOnSuccessReturnVoid()
                .toPostCreateResponse();
    }

    @PostMapping("/{competitionId}/finance-users/{userId}/add")
    public RestResult<Void> addCompetitionFinanceUser(@PathVariable("competitionId") final long competitionId, @PathVariable("userId") final long userId) {
        return competitionSetupFinanceUserService.addFinanceUser(competitionId, userId).toPostResponse();
    }

    @PostMapping("/{competitionId}/finance-users/{userId}/remove")
    public RestResult<Void> removeCompetitionFinanceUser(@PathVariable("competitionId") final long competitionId, @PathVariable("userId") final long userId) {
        return competitionSetupFinanceUserService.removeFinanceUser(competitionId, userId).toPostResponse();
    }

    @GetMapping("/{competitionId}/finance-users/pending-invites")
    public RestResult<List<UserResource>> findPendingCompetitionFinanceUserInvites(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupFinanceUserService.findPendingFinanceUseInvites(competitionId).toGetResponse();
    }
}