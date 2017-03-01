package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryRestService {

    RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(long applicationId);

    RestResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId);

    RestResult<ApplicationAssessorPageResource> getAvailableAssessors(long applicationId, Integer pageIndex, Integer pageSize, Long filterInnovationArea);
}