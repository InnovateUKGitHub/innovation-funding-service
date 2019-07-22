package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.lang.String.format;

/**
 * Implementing class for {@link AssessorCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class AssessorCountSummaryRestServiceImpl extends BaseRestService implements AssessorCountSummaryRestService {

    private static final String ASSESSOR_COUNT_REST_URL = "/assessor-count-summary";

    @Override
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(
            long competitionId, String assessorSearchString, Integer pageIndex, Integer pageSize) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("assessorSearchString", assessorSearchString);

        String uriWithParams = buildPaginationUri(format("%s/find-by-competition-id/%s", ASSESSOR_COUNT_REST_URL, competitionId), pageIndex, pageSize, null, params);
        return getWithRestResult(uriWithParams, AssessorCountSummaryPageResource.class);
    }
}