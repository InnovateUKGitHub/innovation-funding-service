package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;

import static java.lang.String.format;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
public class ApplicationAssessmentSummaryRestServiceImpl extends BaseRestService implements ApplicationAssessmentSummaryRestService {

    private String applicationAssessmentSummaryRestURL = "applicationAssessmentSummary";

    @Override
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
        return getWithRestResult(format("%s/%s", applicationAssessmentSummaryRestURL, applicationId), ApplicationAssessmentSummaryResource.class);
    }

}