package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;

/**
 * Interface for CRUD operations on Monitoring officer related data.
 */
public interface CompetitionSetupMonitoringOfficerRestService {

    RestResult<MonitoringOfficerInviteResource> getMonitoringOfficerInvite(String inviteHash);

    RestResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource);
}