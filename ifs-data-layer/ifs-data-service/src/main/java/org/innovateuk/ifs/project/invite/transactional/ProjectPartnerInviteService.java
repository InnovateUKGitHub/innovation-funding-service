package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectPartnerInviteService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "INVITE_PROJECT_PARTNER_ORGANISATION", securedType = SendProjectPartnerInviteResource.class,
            description = "Only project finance users can invite partner organisations.")
    ServiceResult<Void> invitePartnerOrganisation(long projectId, SendProjectPartnerInviteResource invite);

    ServiceResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId);

    ServiceResult<Void> resendInvite(long inviteId);

    ServiceResult<Void> deleteInvite(long inviteId);
}