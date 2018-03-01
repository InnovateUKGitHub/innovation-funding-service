package org.innovateuk.ifs.assessment.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite}s
 */
public interface InterviewPanelInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_STAGED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewPanelStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can create assessment panel invites for existing users")
    ServiceResult<Void> assignApplications(List<StagedApplicationResource> invites);
}