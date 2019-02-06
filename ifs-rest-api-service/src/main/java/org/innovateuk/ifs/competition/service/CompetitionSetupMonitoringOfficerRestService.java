package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Interface for CRUD operations on Monitoring officer related data.
 */
public interface CompetitionSetupMonitoringOfficerRestService {

    RestResult<Void> inviteMonitoringOfficer(InviteUserResource inviteUserResource, long competitionId);

    RestResult<List<UserResource>> findMonitoringOfficers(long competitionId);

    RestResult<MonitoringOfficerInviteResource> getMonitoringOfficerInvite(String inviteHash);

    RestResult<Void> createMonitoringOfficer(String inviteHash, MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource);
}