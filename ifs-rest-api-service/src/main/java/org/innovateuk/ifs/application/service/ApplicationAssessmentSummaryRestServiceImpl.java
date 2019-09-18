package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * REST service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
@Service
public class ApplicationAssessmentSummaryRestServiceImpl extends BaseRestService implements ApplicationAssessmentSummaryRestService {

    private String applicationAssessmentSummaryRestURL = "/application-assessment-summary";

    @Override
    public RestResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId) {
        return getWithRestResult(format("%s/%s/assigned-assessors", applicationAssessmentSummaryRestURL, applicationId), ParameterizedTypeReferences.applicationAssessorResourceListType());
    }

    @Override
    public RestResult<ApplicationAssessorPageResource> getAvailableAssessors(long applicationId, Integer pageIndex, Integer pageSize, String assessorNameFilter) {
        String uriWithParams = buildUri(applicationAssessmentSummaryRestURL + "/{applicationId}/available-assessors",pageIndex, pageSize, assessorNameFilter, applicationId);
        return getWithRestResult(uriWithParams, ApplicationAssessorPageResource.class);
    }

    @Override
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(long applicationId) {
        return getWithRestResult(format("%s/%s", applicationAssessmentSummaryRestURL, applicationId), ApplicationAssessmentSummaryResource.class);
    }

    protected String buildUri(String url, Integer pageNumber, Integer pageSize, String assessorNameFilter, Object... uriParameters) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if(pageNumber != null) {
            params.put("page", singletonList(pageNumber.toString()));
        }
        if(pageSize != null) {
            params.put("size", singletonList(pageSize.toString()));
        }
        if (assessorNameFilter != null) {
            params.put("assessorNameFilter", singletonList(assessorNameFilter));
        }
        return UriComponentsBuilder.fromPath(url).queryParams(params).buildAndExpand(uriParameters).toUriString();
    }

}