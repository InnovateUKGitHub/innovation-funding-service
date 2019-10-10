package org.innovateuk.ifs.assessment.service.dashboard;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationAssessmentRestService {

    RestResult<ApplicationAssessmentResource> getApplicationAssessmentResource(long competitionId);
}
