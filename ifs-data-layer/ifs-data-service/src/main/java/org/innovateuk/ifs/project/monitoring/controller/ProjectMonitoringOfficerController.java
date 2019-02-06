package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stakeholder controller to handle RESTful services related to project monitoring officers
 */
@RestController
@RequestMapping("/competition/setup")
public class ProjectMonitoringOfficerController {

    @Autowired
    private CompetitionSetupStakeholderService competitionSetupStakeholderService;

    @Autowired
    private RegistrationService registrationService;

//    @PostMapping("/{competitionId}/monitoring-officer/invite")
//    public RestResult<Void> inviteStakeholder(@PathVariable("competitionId") final long competitionId, @RequestBody InviteUserResource inviteUserResource) {
//        return competitionSetupStakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId).toPostResponse();
//    }
//
//    @GetMapping("/{competitionId}/monitoring-officer/find-all")
//    public RestResult<List<UserResource>> findStakeholders(@PathVariable("competitionId") final long competitionId) {
//        return competitionSetupStakeholderService.findStakeholders(competitionId).toGetResponse();
//    }
//
//    @GetMapping("/get-monitoring-officer-invite/{inviteHash}")
//    public RestResult<StakeholderInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash) {
//        return competitionSetupStakeholderService.getInviteByHash(inviteHash).toGetResponse();
//    }
//
//    @PostMapping("/monitoring-officer/create/{inviteHash}")
//    public RestResult<Void> createStakeholder(@PathVariable("inviteHash") String inviteHash, @RequestBody StakeholderRegistrationResource stakeholderRegistrationResource) {
//        return registrationService.createStakeholder(inviteHash, stakeholderRegistrationResource).toPostCreateResponse();
//    }
//
//    @GetMapping("/{competitionId}/stakeholder/pending-invites")
//    public RestResult<List<UserResource>> findPendingStakeholderInvites(@PathVariable("competitionId") final long competitionId) {
//        return competitionSetupStakeholderService.findPendingStakeholderInvites(competitionId).toGetResponse();
//    }
}