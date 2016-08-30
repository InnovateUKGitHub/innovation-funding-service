package com.worth.ifs.assessment.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.security.SecuredBySpring;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for managing {@link com.worth.ifs.invite.domain.CompetitionInvite}s.
 */
public interface CompetitionInviteService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<CompetitionInviteResource> getInvite(@P("inviteHash") String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<CompetitionInviteResource> openInvite(@P("inviteHash") String inviteHash);

    @NotSecured(value = "TODO - ACCEPT_INVITE_ON_HASH - Check the invite either has a user matching the current user or that the invite has no user but the invite email is the same as the email of the current user", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "REJECT_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> rejectInvite(@P("inviteHash") String inviteHash, RejectionReasonResource rejectionReason, String rejectionComment);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_EXISTING_USER_ON_HASH",
            description = "The System Registration user can check for the presence of a User on an invite or the presence of a User with the invited e-mail address",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash);
}
