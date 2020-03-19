package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GrantsInviteService {

    /*
    *
    * who can send invites?
    * */
    ServiceResult<Void> sendInvite(long projectId, SendProjectPartnerInviteResource invite);
    ServiceResult<Void> resendInvite(long inviteId);

    /*
    *
    * will we have a delete invite?
    * */
    ServiceResult<Void> deleteInvite(long inviteId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_INVITE_BY_HASH",
            description = "The System Registration user can get an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<SentProjectPartnerInviteResource> getInviteByHash(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "ACCEPT_INVITE",
            description = "The System Registration user can accept an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptInvite(long inviteId, long organisationId);
}
