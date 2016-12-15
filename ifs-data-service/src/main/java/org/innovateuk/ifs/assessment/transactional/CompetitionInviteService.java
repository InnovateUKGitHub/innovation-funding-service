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

    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('competition_executive')")
    @SecuredBySpring(value = "READ_ASSESSORS_BY_COMPETITION",
            description = "Competition Administrators and Executives can retrieve available assessors by competition",
            additionalComments = "The service additionally checks if the assessor does not have an invite for the competition which is either Pending or Accepted")
    ServiceResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessorCreatedInviteResource>> getCreatedInvites(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<List<AssessorInviteOverviewResource>> getInvitationOverview(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInviteResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendInvite(long inviteId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> deleteInvite(String email, long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> inviteNewUsers(List<NewUserStagedInviteResource> newUserStagedInvites, long competitionId);
}
