package org.innovateuk.ifs.grants.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface GrantsInviteService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "GET_INVITES",
            description = "The Project finance user get all invites for a project")
    ServiceResult<List<SentGrantsInviteResource>> getByProjectId(long projectId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "SEND_INVITE",
            description = "The Project finance user can send an invite")
    ServiceResult<Void> sendInvite(long projectId, GrantsInviteResource invite);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "RESEND_INVITE",
            description = "The Project finance user can re-send an invite")
    ServiceResult<Void> resendInvite(long inviteId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_INVITE_BY_HASH",
            description = "The System Registration user can get an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<SentGrantsInviteResource> getInviteByHash(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "ACCEPT_INVITE",
            description = "The System Registration user can accept an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptInvite(long inviteId);
}
