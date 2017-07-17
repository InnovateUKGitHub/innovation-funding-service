package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * Implementing class for {@link ApplicationCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class ApplicationCountSummaryRestServiceImpl extends BaseRestService implements ApplicationCountSummaryRestService {

    private static final String applicationCountRestUrl = "/applicationCountSummary";

    @Override
    public RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId, Integer pageIndex, Integer pageSize, String filter) {
        String uriWithParams = buildUri(applicationCountRestUrl + "/findByCompetitionId/{compId}", pageIndex, pageSize, filter, competitionId);
        return getWithRestResult(uriWithParams, ApplicationCountSummaryPageResource.class);
    }

    private String buildUri(String url, Integer pageNumber, Integer pageSize, String filter, Object... uriParameters ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (filter != null) {
            params.put("filter", singletonList(filter));
        }
        return buildPaginationUri(url, pageNumber, pageSize, null, params, uriParameters);
    }
}
