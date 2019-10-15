package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

public interface ApplicationAssessmentService {

    ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long userId, long competitionId);
}