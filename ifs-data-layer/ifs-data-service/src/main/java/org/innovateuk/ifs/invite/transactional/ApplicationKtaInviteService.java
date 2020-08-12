package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationKtaInviteService {

    @PreAuthorize("hasPermission(#inviteResource, 'SAVE')")
    ServiceResult<Void> saveKtaInvite(ApplicationKtaInviteResource inviteResource);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationKtaInviteResource> getKtaInviteByApplication(Long applicationId);

    @PreAuthorize("hasPermission(#inviteResource, 'SAVE')")
    ServiceResult<Void> resendKtaInvite(ApplicationKtaInviteResource inviteResource);

    @PreAuthorize("hasPermission(#ktaInviteResourceId, 'org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource', 'DELETE')")
    ServiceResult<Void> removeKtaApplicationInvite(long ktaInviteResourceId);
}
