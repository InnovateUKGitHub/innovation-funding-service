package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;


/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.AssessmentPanelInvite}s.
 */

public interface AssessmentPanelInviteService {


    @SecuredBySpring(value = "GET_ALL_CREATED_INVITES",
            description = "Competition Admins and Project Finance users can get all invites that have been created for an assessment panel on a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId);

    @SecuredBySpring(value = "SEND_ALL_INVITES",
            description = "The Competition Admins and Project Finance users can send all assessment panel invites")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInvitesToSendResource);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available assessors by competition",
            additionalComments = "The available assessors must have accepted a competition invite and not have an assessment panel invite")
    ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance can retrieve available assessor ids by competition",
            additionalComments = "The available assessors must have accepted a competition invite and not have an assessment panel invite")
    ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_INVITES_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve created invites by competition")
    ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "INVITE_EXISTING_USERS",
            description = "The Competition Admin user and Project Finance users can create assessment panel invites for existing users")
    ServiceResult<Void> inviteUsers(List<ExistingUserStagedInviteResource> existingUserStagedInvites);

    @PostFilter("hasPermission(returnObject, 'READ_ASSESSMENT_PANEL_INVITES')")
    @SecuredBySpring(
            value = "READ_ASSESSMENT_PANEL_INVITES",
            description = "An Assessor can view assessor panel invites provided that the invites belong to them")
    ServiceResult<List<AssessmentPanelInviteResource>> getAllInvitesByUser(long userId);
}
