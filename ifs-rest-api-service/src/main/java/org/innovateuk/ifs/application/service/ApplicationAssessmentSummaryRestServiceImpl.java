package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
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
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

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
    public RestResult<ApplicationAvailableAssessorPageResource> getAvailableAssessors(long applicationId, Integer pageIndex, Integer pageSize, String assessorNameFilter, Sort sort) {
        String uriWithParams = buildUri(applicationAssessmentSummaryRestURL + "/{applicationId}/available-assessors",pageIndex, pageSize, assessorNameFilter, sort, applicationId);
        return getWithRestResult(uriWithParams, ApplicationAvailableAssessorPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableAssessorsIds(long applicationId, String assessorName) {
        String uriWithParams = buildUri(applicationAssessmentSummaryRestURL + "/{applicationId}/available-assessors-ids", null, null, assessorName, null, applicationId);
        return getWithRestResult(uriWithParams, longsListType());
    }

    @Override
    public RestResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(long applicationId) {
        return getWithRestResult(format("%s/%s", applicationAssessmentSummaryRestURL, applicationId), ApplicationAssessmentSummaryResource.class);
    }

    protected String buildUri(String url, Integer pageNumber, Integer pageSize, String assessorNameFilter, Sort sort, Object... uriParameters) {
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
        if (assessorNameFilter != null) {
            params.put("assessorNameFilter", singletonList(assessorNameFilter));
        }
        if (sort != null) {
            params.put("sort", singletonList(sort.name()));
        }
        return UriComponentsBuilder.fromPath(url).queryParams(params).buildAndExpand(uriParameters).toUriString();
    }

}