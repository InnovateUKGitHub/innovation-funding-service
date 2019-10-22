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

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_PROJECT_PARTNER_INVITES", securedType = SendProjectPartnerInviteResource.class,
            description = "Only internal users can see project partner invites.")
    ServiceResult<List<SentProjectPartnerInviteResource>> getPartnerInvites(long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "INVITE_PROJECT_PARTNER_ORGANISATION", securedType = SendProjectPartnerInviteResource.class,
            description = "Only project finance users can invite partner organisations.")
    ServiceResult<Void> resendInvite(long inviteId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "INVITE_PROJECT_PARTNER_ORGANISATION", securedType = SendProjectPartnerInviteResource.class,
            description = "Only project finance users can invite partner organisations.")
    ServiceResult<Void> deleteInvite(long inviteId);
}