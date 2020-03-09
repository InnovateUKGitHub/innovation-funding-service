package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.AccUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;

public interface AccUserRegistrationRestService {

    RestResult<AccMonitoringOfficerInviteResource> getAccMonitoringOfficerInvite(String inviteHash);

    RestResult<AccMonitoringOfficerInviteResource> openAccMonitoringOfficerInvite(String inviteHash);

    RestResult<Void> createAccMonitoringOfficer(String inviteHash, AccUserRegistrationResource accUserRegistrationResource);

}
