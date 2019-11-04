package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PostFilter;

public interface AssessmentCompetitionDashboardService {

    @PostFilter("hasPermission(filterObject, 'READ_DASHBOARD')")
    ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long userId, long competitionId);
}