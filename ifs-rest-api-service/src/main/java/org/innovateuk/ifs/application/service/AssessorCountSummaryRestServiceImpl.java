package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Collections.singletonList;

/**
 * Implementing class for {@link AssessorCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class AssessorCountSummaryRestServiceImpl extends BaseRestService implements AssessorCountSummaryRestService {

    private static final String applicationCountRestUrl = "/assessorCountSummary";

    @Override
    public RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Integer pageIndex, Integer pageSize, String filter) {
        String uriWithParams = buildUri(applicationCountRestUrl + "/findByCompetitionId/{compId}", pageIndex, pageSize, filter, competitionId);
        return getWithRestResult(uriWithParams, AssessorCountSummaryPageResource.class);
    }

    private String buildUri(String url, Integer pageNumber, Integer pageSize, String filter, Object... uriParameters ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (filter != null) {
            params.put("filter", singletonList(filter));
        }
        return buildPaginationUri(url, pageNumber, pageSize, null, params, uriParameters);
    }
}