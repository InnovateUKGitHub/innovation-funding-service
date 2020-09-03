package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;

/**
 * Stakeholder controller to handle RESTful services related to stakeholders
 */
@RestController
@RequestMapping("/competition/setup")
public class CompetitionSetupStakeholderController {

    @Autowired
    private CompetitionSetupStakeholderService competitionSetupStakeholderService;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/{competitionId}/stakeholder/invite")
    public RestResult<Void> inviteStakeholder(@PathVariable("competitionId") final long competitionId, @RequestBody InviteUserResource inviteUserResource) {
        return competitionSetupStakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId).toPostResponse();
    }

    @GetMapping("/{competitionId}/stakeholder/find-all")
    public RestResult<List<UserResource>> findStakeholders(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupStakeholderService.findStakeholders(competitionId).toGetResponse();
    }

    @GetMapping("/get-stakeholder-invite/{inviteHash}")
    public RestResult<StakeholderInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash) {
        return competitionSetupStakeholderService.getInviteByHash(inviteHash).toGetResponse();
    }

    @PostMapping("/stakeholder/create/{inviteHash}")
    public RestResult<Void> createStakeholder(@PathVariable("inviteHash") String inviteHash, @RequestBody StakeholderRegistrationResource stakeholderRegistrationResource) {
        return registrationService.createUser(anUserCreationResource()
            .withFirstName(stakeholderRegistrationResource.getFirstName())
            .withLastName(stakeholderRegistrationResource.getLastName())
            .withPassword(stakeholderRegistrationResource.getPassword())
            .withInviteHash(inviteHash)
            .withRole(Role.STAKEHOLDER)
            .build())
            .andOnSuccessReturnVoid()
            .toPostCreateResponse();
    }

    @PostMapping("/{competitionId}/stakeholder/{stakeholderUserId}/add")
    public RestResult<Void> addStakeholder(@PathVariable("competitionId") final long competitionId, @PathVariable("stakeholderUserId") final long stakeholderUserId) {
        return competitionSetupStakeholderService.addStakeholder(competitionId, stakeholderUserId).toPostResponse();
    }

    @PostMapping("/{competitionId}/stakeholder/{stakeholderUserId}/remove")
    public RestResult<Void> removeStakeholder(@PathVariable("competitionId") final long competitionId, @PathVariable("stakeholderUserId") final long stakeholderUserId) {
        return competitionSetupStakeholderService.removeStakeholder(competitionId, stakeholderUserId).toPostResponse();
    }

    @GetMapping("/{competitionId}/stakeholder/pending-invites")
    public RestResult<List<UserResource>> findPendingStakeholderInvites(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupStakeholderService.findPendingStakeholderInvites(competitionId).toGetResponse();
    }
}