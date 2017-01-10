package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId);

}