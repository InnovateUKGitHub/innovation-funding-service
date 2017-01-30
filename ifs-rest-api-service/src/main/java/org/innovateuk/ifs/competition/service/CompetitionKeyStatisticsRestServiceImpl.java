package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;
import org.springframework.stereotype.Service;

/**
 * Interface for retrieving {@link CompetitionKeyStatisticsResource}
 */
@Service
public class CompetitionKeyStatisticsRestServiceImpl extends BaseRestService implements CompetitionKeyStatisticsRestService {

    private String competitionKeyStatisticsRestURL = "/competitionStatistics";

    @Override
    public RestResult<CompetitionKeyStatisticsResource> getKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(competitionKeyStatisticsRestURL + "/" + competitionId, CompetitionKeyStatisticsResource.class);
    }
}
