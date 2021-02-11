package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around monitoring officer invites.
 */
public interface MonitoringOfficerInviteService {

    @SecuredBySpring(value = "SAVE_MONITORING_OFFICER_INVITE", description = "Only comp admin, project finance or IFS admin can save a monitoring officer invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    ServiceResult<Void> inviteMonitoringOfficer(User invitedUser, Project project);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_MONITORING_OFFICER_INVITE_ON_HASH",
            description = "The System Registration user can activate a user based on their invite hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<User> activateUserByHash(String hash, MonitoringOfficerRegistrationResource monitoringOfficerRegistrationResource);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_MONITORING_OFFICER_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<MonitoringOfficerInviteResource> getInviteByHash(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_MONITORING_OFFICER_INVITE_ON_HASH",
            description = "The System Registration user can open an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<MonitoringOfficerInviteResource> openInvite(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_MONITORING_OFFICER_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkUserExistsForInvite(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_MONITORING_OFFICER_INVITE_ON_HASH",
            description = "The System Registration user and add the monitoring officer role to a user specific by the invite",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<User> addMonitoringOfficerRole(String hash);

}