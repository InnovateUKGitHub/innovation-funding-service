package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryRestService {

    RestResult<List<ApplicationAssessorResource>> getAssessors(Long applicationId);

    RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId);

}