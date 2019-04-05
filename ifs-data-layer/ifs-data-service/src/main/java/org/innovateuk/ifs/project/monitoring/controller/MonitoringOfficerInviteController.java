package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerInviteService;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle RESTful services related to inviting project monitoring officers
 */
@RestController
@RequestMapping("/monitoring-officer-registration")
public class MonitoringOfficerInviteController {

    private MonitoringOfficerInviteService monitoringOfficerInviteService;
    private RegistrationService registrationService;
    private CrmService crmService;
    private UserService userService;

    public MonitoringOfficerInviteController(MonitoringOfficerInviteService monitoringOfficerInviteService,
                                             RegistrationService registrationService,
                                             CrmService crmService,
                                             UserService userService) {
        this.monitoringOfficerInviteService = monitoringOfficerInviteService;
        this.registrationService = registrationService;
        this.crmService = crmService;
        this.userService = userService;
    }

    @GetMapping("/get-monitoring-officer-invite/{inviteHash}")
    public RestResult<MonitoringOfficerInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash) {
        return monitoringOfficerInviteService.getInviteByHash(inviteHash).toGetResponse();
    }

    @GetMapping("/open-monitoring-officer-invite/{inviteHash}")
    public RestResult<MonitoringOfficerInviteResource> openInvite(@PathVariable("inviteHash") String inviteHash) {
        return monitoringOfficerInviteService.openInvite(inviteHash).toGetResponse();
    }

    @PostMapping("/create-monitoring-officer")
    public RestResult<Void> createPendingMonitoringOfficer(@RequestBody MonitoringOfficerCreateResource resource) {

        User user = new User();
        user.setFirstName(resource.getFirstName());
        user.setLastName(resource.getLastName());
        user.setPhoneNumber(resource.getPhoneNumber());
        user.setEmail(resource.getEmailAddress());
        return registrationService.createPendingUser(user)
                .andOnSuccess(pendingUser -> userService.grantRole(new GrantRoleCommand(pendingUser.getId(),
                                                                              Role.MONITORING_OFFICER)))
                .toPostResponse();
    }

    @PostMapping("/monitoring-officer/create/{inviteHash}")
    public RestResult<Void> createMonitoringOfficer(@PathVariable("inviteHash") String inviteHash, @RequestBody MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource) {
        return monitoringOfficerInviteService
                .activateUserByHash(inviteHash, monitoringOfficerRegistrationResource)
                .andOnSuccess(user -> crmService.syncCrmContact(user.getId()))
                .toPostResponse();
    }

    @GetMapping("/monitoring-officer/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable("inviteHash") String hash) {
        return monitoringOfficerInviteService.checkUserExistsForInvite(hash).toGetResponse();
    }

    @PostMapping("/monitoring-officer/add-monitoring-officer-role/{inviteHash}")
    public RestResult<Void> addMonitoringOfficerRole(@PathVariable("inviteHash") String hash) {
        return monitoringOfficerInviteService.addMonitoringOfficerRole(hash).toPostResponse();
    }
}