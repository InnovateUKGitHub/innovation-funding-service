package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

/**
 * Implementing class for {@link ApplicationCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class ApplicationCountSummaryRestServiceImpl extends BaseRestService implements ApplicationCountSummaryRestService {

    private static final String APPLICATION_COUNT_REST_URL = "/application-count-summary";

    @Override
    public RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                       int pageIndex,
                                                                                                       int pageSize,
                                                                                                       String filter) {
        String uriWithParams = buildUri(APPLICATION_COUNT_REST_URL + "/find-by-competition-id/{compId}", pageIndex, pageSize, filter, competitionId);
        return getWithRestResult(uriWithParams, ApplicationCountSummaryPageResource.class);
    }

    @Override
    public RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorId(long competitionId,
                                                                                                                        long assessorId,
                                                                                                                        int page,
                                                                                                                        Sort sort,
                                                                                                                        String filter) {

        String baseUrl = format("%s/%s/%d/%d", APPLICATION_COUNT_REST_URL, "find-by-competition-id-and-assessor-id", competitionId, assessorId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page)
                .queryParam("filter", filter)
                .queryParam("sort", sort);
        return getWithRestResult(builder.toUriString(), ApplicationCountSummaryPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(long competitionId, long assessorId, String filter) {
        String baseUrl = format("%s/%s/%d/%d", APPLICATION_COUNT_REST_URL, "find-ids-by-competition-id-and-assessor-id", competitionId, assessorId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("filter", filter);

        return getWithRestResult(builder.toUriString(), longsListType());
    }

    private String buildUri(String url, Integer pageNumber, Integer pageSize, String filter, Object... uriParameters ) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (filter != null) {
            params.put("filter", singletonList(filter));
        }
        return buildPaginationUri(url, pageNumber, pageSize, null, params, uriParameters);
    }
}
