package org.innovateuk.ifs.invite.transactional;

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
    ServiceResult<ApplicationKtaInviteResource> getKtaInviteByHash(String hash);

    @PreAuthorize("hasPermission(#hash, 'org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource', 'VIEW_AND_ACCEPT')")
    ServiceResult<Void> acceptInvite(String hash);

}
