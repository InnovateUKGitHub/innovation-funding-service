package org.innovateuk.ifs.project.monitor.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitor.transactional.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle RESTful services related to project monitoring officers
 */
@RestController
@RequestMapping("/competition/setup")
public class ProjectMonitoringOfficerController {

    @Autowired
    private ProjectMonitoringOfficerService projectMonitoringOfficerService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    public ProjectMonitoringOfficerController(ProjectMonitoringOfficerService projectMonitoringOfficerService, RegistrationService registrationService) {
        this.projectMonitoringOfficerService = projectMonitoringOfficerService;
        this.registrationService = registrationService;
    }

    @GetMapping("/get-monitoring-officer-invite/{inviteHash}")
    public RestResult<MonitoringOfficerInviteResource> getInvite(@PathVariable("inviteHash") String inviteHash) {
        return projectMonitoringOfficerService.getInviteByHash(inviteHash).toGetResponse();
    }

    @GetMapping("/open-monitoring-officer-invite/{inviteHash}")
    public RestResult<MonitoringOfficerInviteResource> openInvite(@PathVariable("inviteHash") String inviteHash) {
        return projectMonitoringOfficerService.openInvite(inviteHash).toGetResponse();
    }


    @PostMapping("/monitoring-officer/create/{inviteHash}")
    public RestResult<Void> createMonitoringOfficer(@PathVariable("inviteHash") String inviteHash, @RequestBody MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource) {
        return registrationService.createMonitoringOfficer(inviteHash, monitoringOfficerRegistrationResource).toPostResponse();
    }

    @GetMapping("/monitoring-officer/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable("inviteHash") String hash) {
        return projectMonitoringOfficerService.checkUserExistsForInvite(hash).toGetResponse();
    }

    @PostMapping("/monitoring-officer/add-monitoring-officer-role/{inviteHash}")
    public RestResult<Void> addMonitoringOfficerRole(@PathVariable("inviteHash") String hash) {
        return projectMonitoringOfficerService.addMonitoringOfficerRole(hash).toPostResponse();
    }
}