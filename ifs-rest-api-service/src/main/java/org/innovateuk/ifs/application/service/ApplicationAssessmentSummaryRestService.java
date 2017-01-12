package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryRestService {

    RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId);

}