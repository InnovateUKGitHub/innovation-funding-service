package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class CompetitionSetupMonitoringOfficerRestServiceImpl extends BaseRestService implements CompetitionSetupMonitoringOfficerRestService {

    private static final String BASE_URL = "/competition/setup";

    @Override
    public RestResult<MonitoringOfficerInviteResource> getMonitoringOfficerInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/get-monitoring-officer-invite/%s", BASE_URL, inviteHash), MonitoringOfficerInviteResource.class);
    }

    @Override
    public RestResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource) {
            String url = format("%s/monitoring-officer/create/%s", BASE_URL, inviteHash);
            return postWithRestResultAnonymous(url, monitoringOfficerRegistrationResource, Void.class);
    }
}