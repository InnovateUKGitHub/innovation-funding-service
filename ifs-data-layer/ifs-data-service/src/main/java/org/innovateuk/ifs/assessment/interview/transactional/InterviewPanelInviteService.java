package org.innovateuk.ifs.assessment.interview.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service for managing {@link org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite}s
 */
public interface InterviewPanelInviteService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_AVAILABLE_ASSESSORS_BY_COMPETITION",
            description = "Competition Admins and Project Finance users can retrieve available applications by competition")
    ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable);
}
