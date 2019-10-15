package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AssessorCompetitionDashboardService {

    @PreAuthorize("hasAuthority('assessor')")
    @SecuredBySpring(value = "ASSESSOR_COMPETITION_DASHBOARD",
            description = "The Assessor can view the applications in competition")
    ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId);
}