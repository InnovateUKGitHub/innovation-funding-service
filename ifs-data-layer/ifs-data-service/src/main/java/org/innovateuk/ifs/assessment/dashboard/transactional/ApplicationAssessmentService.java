package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

public interface ApplicationAssessmentService {

    ServiceResult<List<ApplicationAssessmentResource>> getApplicationAssessmentResource(long userId, long competitionId);
}
