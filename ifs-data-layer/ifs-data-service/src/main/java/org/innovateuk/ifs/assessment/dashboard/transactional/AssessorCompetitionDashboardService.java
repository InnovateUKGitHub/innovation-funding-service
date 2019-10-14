package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

public interface AssessorCompetitionDashboardService {

    ServiceResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboardResource(long competitionId, List<ApplicationAssessmentResource> applicationAssessmentResource);
}
