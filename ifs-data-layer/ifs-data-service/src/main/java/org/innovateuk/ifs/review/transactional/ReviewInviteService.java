package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing {@link ReviewInvite}s.
 */
public interface ReviewInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "GET_ALL_CREATED_PANEL_INVITES",
            description = "Competition Admins and Project Finance users can get all invites that have been created for an assessment panel on a competition")
    ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "GET_ALL_PANEL_INVITES_TO_RESEND",
            description = "Competition Admins and Project Finance users can get all invites to be resent for an assessment panel on a competition")
    ServiceResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "SEND_ALL_PANEL_INVITES",
            description = "The Competition Admins and Project Finance users can send all assessment panel invites")
    ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInvitesToSendResource);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "RESEND_PANEL_INVITES",
            description = "The Competition Admins and Project Finance users can resend assessment panel invites")
    ServiceResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available assessors by competition",
            additionalComments = "The available assessors must have accepted a competition invite and not have an assessment panel invite")
    ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSOR_IDS_BY_COMPETITION",
            description = "Competition Admins and Project Finance can retrieve available assessor ids by competition",
            additionalComments = "The available assessors must have accepted a competition invite and not have an assessment panel invite")
    ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ_PANEL_INVITES_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve created invites by competition")
    ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "PANEL_INVITE_EXISTING_USERS",
            description = "The Competition Admin user and Project Finance users can create assessment panel invites for existing users")
    ServiceResult<Void> inviteUsers(List<ExistingUserStagedInviteResource> existingUserStagedInvites);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'READ_ASSESSMENT_PANEL_INVITES')")
    @SecuredBySpring(value = "READ_ASSESSMENT_PANEL_INVITES",
            description = "An Assessor can view assessor panel invites provided that the invites belong to them")
    ServiceResult<List<ReviewParticipantResource>> getAllInvitesByUser(long userId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ_PANEL_INVITE_OVERVIEW_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve assessment panel invitation overview by competition")
    ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                            Pageable pageable,
                                                                            List<ParticipantStatus> statuses);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ_NON_ACCEPTED_INVITE_IDS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve invited assessor invite ids by competition")
    ServiceResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_PANEL_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<ReviewInviteResource> openInvite(String inviteHash);

    @PreAuthorize("hasPermission(#inviteHash, 'org.innovateuk.ifs.invite.resource.ReviewParticipantResource', 'ACCEPT')")
    @SecuredBySpring(value = "ACCEPT_PANEL_INVITE_ON_HASH",
            description = "An Assessor can accept a given hash provided that they are the same user as the ReviewParticipant",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "REJECT_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> rejectInvite(String inviteHash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_EXISTING_USER_ON_HASH",
            description = "The System Registration user can check for the presence of a User on an invite or the presence of a User with the invited e-mail address",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkUserExistsForInvite(String inviteHash);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "DELETE_INVITE",
            description = "The Competition Admins and Project Finance users can delete an assessment panel invite")
    ServiceResult<Void> deleteInvite(String email, long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "DELETE_ALL_INVITES",
            description = "The Competition Admins and Project Finance users can delete all the assessment panel invites")
    ServiceResult<Void> deleteAllInvites(long competitionId);
}
