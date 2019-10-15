package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectPartnerInviteService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "INVITE_PROJECT_PARTNER_ORGANISATION", securedType = ProjectPartnerInviteResource.class,
            description = "Only project finance users can invite partner organisations.")
    ServiceResult<Void> invitePartnerOrganisation(long projectId, ProjectPartnerInviteResource invite);
}