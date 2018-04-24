package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
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

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can view template for inviting applicants")
    ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "STAGE_INTERVIEW_PANEL_APPLICATIONS",
            description = "The Competition Admin user and Project Finance users can send invites to applicants")
    ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "IS_APPLICATION_ASSIGNED_TO_INTERVIEW",
            description = "The applicants can see if their application is assigned to interview")
    ServiceResult<Boolean> isApplicationAssigned(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewAssignmentKeyStatisticsResource> getKeyStatistics(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPLOAD_FEEDBACK",
            description = "Competition Admins and Project Finance users can upload feedback")
    ServiceResult<Void> uploadFeedback(String contentType, String contentLength, String originalFilename, long applicationId,
                            HttpServletRequest request);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "DELETE_FEEDBACK",
            description = "Competition Admins and Project Finance users can delete feedback")
    ServiceResult<Void> deleteFeedback(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "DOWNLOAD_FEEDBACK",
            description = "Competition Admins and Project Finance users can download feedback")
    ServiceResult<FileAndContents> downloadFeedback(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "FIND_FEEDBACK",
            description = "Competition Admins and Project Finance users can find feedback")
    ServiceResult<FileEntryResource> findFeedback(Long applicationId);
}