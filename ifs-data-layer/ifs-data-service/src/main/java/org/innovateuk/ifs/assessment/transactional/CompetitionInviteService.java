package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.CompetitionInvite}s.
 */
public interface CompetitionInviteService {

    @SecuredBySpring(value = "GET_ALL_CREATED_INVITES",
            description = "Competition Admins and Project Finance users can get all invites that have been created for a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    @SecuredBySpring(value = "GET_INVITE",
            description = "The Competition Admin user, or the Competition Executive user can get a competition invite that has been created")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<AssessorInvitesToSendResource> getInviteToSend(long inviteId);

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

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available assessors by competition",
            additionalComments = "The service additionally checks if the assessor does not have an invite for the competition which is either Pending or Accepted")
    ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable, Optional<Long> innovationArea);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance can retrieve available assessor ids by competition",
            additionalComments = "The service additionally checks if the assessor does not have an invite for the competition which is either Pending or Accepted")
    ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId, Optional<Long> innovationArea);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_INVITES_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve created invites by competition")
    ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_INVITE_OVERVIEW_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve invitation overview by competition")
    ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                            Pageable pageable,
                                                                            Optional<Long> innovationArea,
                                                                            List<ParticipantStatus> statuses,
                                                                            Optional<Boolean> compliant);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_INVITE_OVERVIEW_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve invitation statistics by competition")
    ServiceResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "INVITE_NEW_USER",
            description = "The Competition Admins and Project Finance users can create a competition invite for a new user")
    ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "INVITE_NEW_USERS",
            description = "The Competition Admins and Project Finance users can create competition invites for new users")
    ServiceResult<Void> inviteNewUsers(List<NewUserStagedInviteResource> newUserStagedInvites, long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "INVITE_EXISTING_USER",
            description = "The Competition Admins and Project Finance users can create a competition invite for an existing user")
    ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInviteResource);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "INVITE_EXISTING_USERS",
            description = "The Competition Admin user and Project Finance users can create competition invites for existing users")
    ServiceResult<Void> inviteUsers(List<ExistingUserStagedInviteResource> existingUserStagedInvites);

    @SecuredBySpring(value = "SEND_ALL_INVITES",
            description = "The Competition Admins and Project Finance users can send all competition invites")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInvitesToSendResource);

    @SecuredBySpring(value = "RESEND_INVITE",
            description = "The Competition Admin user, or the Competition Executive user can send a competition invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> resendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource);

    @SecuredBySpring(value = "DELETE_INVITE",
            description = "The Competition Admins and Project Finance users can delete a competition invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> deleteInvite(String email, long competitionId);

    @SecuredBySpring(value = "DELETE_ALL_INVITES",
            description = "The Competition Admins and Project Finance users can delete all the competition invites")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> deleteAllInvites(long competitionId);
}
