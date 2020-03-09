package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.AccUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AccUserRegistrationServiceRestImpl extends BaseRestService implements AccUserRegistrationRestService {

    private static final String BASE_URL = "/acc-monitoring-officer-registration";

    @Override
    public RestResult<AccMonitoringOfficerInviteResource> getAccMonitoringOfficerInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/get-monitoring-officer-invite/%s", BASE_URL, inviteHash), AccMonitoringOfficerInviteResource.class);
    }

    @Override
    public RestResult<AccMonitoringOfficerInviteResource> openAccMonitoringOfficerInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/open-monitoring-officer-invite/%s", BASE_URL, inviteHash), AccMonitoringOfficerInviteResource.class);
    }

    @Override
    public RestResult<Void> createAccMonitoringOfficer(String inviteHash, AccUserRegistrationResource accUserRegistrationResource) {
        String url = format("%s/create/%s", BASE_URL, inviteHash);
        return postWithRestResultAnonymous(url, accUserRegistrationResource, Void.class);
    }
}
