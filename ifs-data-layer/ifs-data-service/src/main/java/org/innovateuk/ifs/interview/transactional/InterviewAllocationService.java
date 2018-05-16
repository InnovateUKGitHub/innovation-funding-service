package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.resource.InterviewNotifyAllocationResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for allocating applications to assessors in interview panels
 */
public interface InterviewAllocationService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_INTERVIEW_PANEL_INVITES_ACCEPTED_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve interview panel assessors who have accepted for allocating applications to them by competition")
    ServiceResult<InterviewAcceptedAssessorsPageResource> getInterviewAcceptedAssessors(long competitionId,
                                                                                        Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_UNALLOCATED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve unallocated applications")
    ServiceResult<InterviewApplicationPageResource> getUnallocatedApplications(long competition,
                                                                               long assessorUserId,
                                                                               Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ALLOCATED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve allocated applications")
    ServiceResult<InterviewApplicationPageResource> getAllocatedApplications(long competition,
                                                                               long assessorUserId,
                                                                               Pageable pageable);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ALLOCATED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve allocated applications")
    ServiceResult<List<InterviewApplicationResource>> getAllocatedApplications(List<Long> applicationIds);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_ALLOCATED_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve allocated applications")
    ServiceResult<AssessorInvitesToSendResource> getInviteToSend(long competitionId, long assessorId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_UNALLOCATED_APPLICATION_IDS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve unallocated applications")
    ServiceResult<List<Long>> getUnallocatedApplicationIds(long competitionId, long assessorId);


    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_UNALLOCATED_APPLICATION_IDS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can allocate applications")
    ServiceResult<Void> sendInvite(InterviewNotifyAllocationResource interviewNotifyAllocationResource);
}
