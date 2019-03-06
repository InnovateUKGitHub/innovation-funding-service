package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class MonitoringOfficerRegistrationRestServiceImpl extends BaseRestService implements MonitoringOfficerRegistrationRestService {

    private static final String BASE_URL = "/monitoring-officer-registration";

    @Override
    public RestResult<MonitoringOfficerInviteResource> getMonitoringOfficerInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/get-monitoring-officer-invite/%s", BASE_URL, inviteHash), MonitoringOfficerInviteResource.class);
    }

    @Override
    public RestResult<MonitoringOfficerInviteResource> openMonitoringOfficerInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/open-monitoring-officer-invite/%s", BASE_URL, inviteHash), MonitoringOfficerInviteResource.class);
    }

    @Override
    public RestResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource) {
            String url = format("%s/monitoring-officer/create/%s", BASE_URL, inviteHash);
            return postWithRestResultAnonymous(url, monitoringOfficerRegistrationResource, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/monitoring-officer/check-existing-user/%s", BASE_URL, inviteHash), Boolean.class);
    }

    @Override
    public RestResult<Void> addMonitoringOfficerRole(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/monitoring-officer/add-monitoring-officer-role/%s", BASE_URL, inviteHash));
    }
}