package org.innovateuk.ifs.assessment.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInvitePageResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite}s
 */
public interface InterviewPanelInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewPanelCreatedInvitePageResource> getCreatedApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "PANEL_INVITE_EXISTING_USERS",
            description = "The Competition Admin user and Project Finance users can create assessment panel invites for existing users")
    ServiceResult<Void> assignApplications(List<ExistingUserStagedInviteResource> invites);
}
