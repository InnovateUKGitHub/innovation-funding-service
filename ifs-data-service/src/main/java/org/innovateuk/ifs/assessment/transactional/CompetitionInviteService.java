package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.CompetitionInvite}s.
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

    @PreAuthorize("hasPermission(#inviteHash, 'org.innovateuk.ifs.invite.resource.CompetitionParticipantResource', 'ACCEPT')")
    @SecuredBySpring(value = "ACCEPT_INVITE_ON_HASH",
            description = "An Assessor can accept a given hash provided that they are the same user as the CompetitionParticipant",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash, UserResource userResource);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "REJECT_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> rejectInvite(@P("inviteHash") String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_EXISTING_USER_ON_HASH",
            description = "The System Registration user can check for the presence of a User on an invite or the presence of a User with the invited e-mail address",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessorCreatedInviteResource>> getCreatedInvites(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessorInviteOverviewResource>> getInvitationOverview(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('competition_executive')")
    @SecuredBySpring(value = "INVITE_NEW_USER",
            description = "The Competition Admin user, or the Competition Executive user can create a competition invite for a new user")
    ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite);

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('competition_executive')")
    @SecuredBySpring(value = "INVITE_EXISTING_USER",
            description = "The Competition Admin user, or the Competition Executive user can create a competition invite for an existing user")
    ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInviteResource);

    @SecuredBySpring(value = "SEND_INVITE",
            description = "The Competition Admin user, or the Competition Executive user can send a competition invite")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('competition_executive')")
    ServiceResult<Void> sendInvite(long inviteId);

    @SecuredBySpring(value = "DELETE_INVITE",
            description = "The Competition Admin user, or the Competition Executive user can delete a competition invite")
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('competition_executive')")
    ServiceResult<Void> deleteInvite(String email, long competitionId);
}
