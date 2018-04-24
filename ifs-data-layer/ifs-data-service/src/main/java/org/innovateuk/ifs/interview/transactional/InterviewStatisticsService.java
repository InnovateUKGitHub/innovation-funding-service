package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service to get statistics related to Interview Panels.
 */
@Service
public interface InterviewStatisticsService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_APPLICATIONS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<InterviewAssignmentKeyStatisticsResource> getInterviewPanelKeyStatistics(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "READ_INTERVIEW_INVITE_STATISTICS",
            description = "Comp admins and project finance users can see invite statistics for the assessment interview panel invite page")
    ServiceResult<InterviewInviteStatisticsResource> getInterviewInviteStatistics(long competitionId);
}