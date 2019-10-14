package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectPartnerInviteService {

    @PreAuthorize("hasAnyAuthority('ifs_administrator', 'support')")
    @SecuredBySpring(value = "INVITE_PROJECT_PARTNER_ORGANISATION", securedType = ProjectPartnerInviteResource.class,
            description = "Only admins and support users can invite partner organisations." )
    ServiceResult<Void> invitePartnerOrganisation(long projectId, ProjectPartnerInviteResource invite);
}