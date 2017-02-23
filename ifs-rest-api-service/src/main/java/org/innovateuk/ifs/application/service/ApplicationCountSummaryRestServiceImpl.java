package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationCountSummaryResourceListType;

/**
 * Implementing class for {@link ApplicationCountSummaryRestService}, for the action on retrieving application statistics.
 */
@Service
public class ApplicationCountSummaryRestServiceImpl extends BaseRestService implements ApplicationCountSummaryRestService {

    private static final String applicationCountRestUrl = "/applicationCountSummary";

    @Override
    public RestResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId) {
        return getWithRestResult(format("%s/%s/%s", applicationCountRestUrl, "findByCompetitionId", competitionId), applicationCountSummaryResourceListType());
    }
}
