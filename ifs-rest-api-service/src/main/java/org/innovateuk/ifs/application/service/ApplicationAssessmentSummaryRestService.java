package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public interface ApplicationAssessmentSummaryRestService {

    RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(long applicationId);

    RestResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId);

    RestResult<ApplicationAvailableAssessorPageResource> getAvailableAssessors(long applicationId, Integer pageIndex, Integer pageSize, String assessorNameFilter, Sort sort);
}