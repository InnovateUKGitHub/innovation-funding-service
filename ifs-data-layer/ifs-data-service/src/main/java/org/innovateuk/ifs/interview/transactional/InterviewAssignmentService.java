package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing {@link InterviewInvite}s
 */
public interface InterviewAssignmentService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_STAGED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ASSIGNED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewAssignmentApplicationPageResource> getAssignedApplications(long competitionId, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can create assessment panel invites for existing users")
    ServiceResult<Void> assignApplications(List<StagedApplicationResource> invites);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UNSTAGE_INTERVIEW_PANEL_APPLICATION",
            description = "The Competition Admin user and Project Finance users can unstage applications")
    ServiceResult<Void> unstageApplication(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UNSTAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can unstage applications")
    ServiceResult<Void> unstageApplications(long competitionId);

    @PreAuthorize("hasAnyAuthority('applicant', 'assessor', 'comp_admin', 'project_finance', 'innovation_lead', 'support')")
    @SecuredBySpring(value = "IS_APPLICATION_ASSIGNED_TO_INTERVIEW",
            description = "The applicants, assessors, Comp exec, project finance, innovation lead and support users can see if the application is assigned to interview")
    ServiceResult<Boolean> isApplicationAssigned(long applicationId);

}