package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Implementing class for {@link AssessorCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class AssessorCountSummaryRestServiceImpl extends BaseRestService implements AssessorCountSummaryRestService {

    private static final String assessorCountRestUrl = "/assessorCountSummary";

    @Override
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Integer pageIndex, Integer pageSize) {
        String uriWithParams = buildUri(assessorCountRestUrl + "/findByCompetitionId/{compId}", pageIndex, pageSize, competitionId);
        return getWithRestResult(uriWithParams, AssessorCountSummaryPageResource.class);
    }

    private String buildUri(String url, Integer pageNumber, Integer pageSize, Object... uriParameters ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        return buildPaginationUri(url, pageNumber, pageSize, null, params, uriParameters);
    }
}