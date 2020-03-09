package org.innovateuk.ifs.invite.controller;


import org.innovateuk.ifs.acc.AccMonitoringOfficerInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.registration.resource.AccUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.GET_INVITE_BY_HASH;
import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.PROJECT_INVITE_BASE_URL;

@RestController
@RequestMapping("/acc-monitoring-officer-registration")
public class AccMonitoringOfficerInviteController {

    @Autowired
    private AccMonitoringOfficerInviteService accMonitoringOfficerInviteService;

    @Autowired
    private CrmService crmService;

    @GetMapping("/get-monitoring-officer-invite/" + "{hash}")
    public RestResult<AccMonitoringOfficerInviteResource> getAccMonitoringOfficerInviteByHash(@PathVariable("hash") String hash) {
        return accMonitoringOfficerInviteService.getInviteByHash(hash).toGetResponse();
    }

    @GetMapping("/open-monitoring-officer-invite/{hash}")
    public RestResult<AccMonitoringOfficerInviteResource> openAccMonitoringOfficerInviteByHash(@PathVariable("hash") String hash) {
        return accMonitoringOfficerInviteService.openInvite(hash).toGetResponse();
    }

    @PostMapping("/create/{inviteHash}")
    public RestResult<Void> createMonitoringOfficer(@PathVariable("inviteHash") String inviteHash, @RequestBody AccUserRegistrationResource accUserRegistrationResource) {
        return accMonitoringOfficerInviteService
                .activateUserByHash(inviteHash, accUserRegistrationResource)
                .andOnSuccess(user -> crmService.syncCrmContact(user.getId()))
                .toPostResponse();
    }
}
