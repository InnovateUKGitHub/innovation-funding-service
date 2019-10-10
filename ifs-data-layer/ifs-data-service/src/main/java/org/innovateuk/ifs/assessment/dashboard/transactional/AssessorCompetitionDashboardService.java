package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface AssessorCompetitionDashboardService {

    ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboard(long userId, long competitionId);
}
