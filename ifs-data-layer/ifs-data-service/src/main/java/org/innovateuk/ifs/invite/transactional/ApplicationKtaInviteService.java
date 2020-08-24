package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationKtaInviteService {

    @PreAuthorize("hasPermission(#inviteResource, 'SAVE')")
    ServiceResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationKtaInviteResource> getKtaInviteByApplication(long applicationId);

    @PreAuthorize("hasPermission(#inviteResource, 'SAVE')")
    ServiceResult<Void> resendKtaInvite(ApplicationKtaInviteResource inviteResource);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> removeKtaInviteByApplication(long applicationId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<ApplicationKtaInviteResource> getKtaInviteByHash(String hash);

    @PreAuthorize("hasPermission(#hash, 'org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource', 'ACCEPT')")
    ServiceResult<Void> acceptInvite(String hash);

}
