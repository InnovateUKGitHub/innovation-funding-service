package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;

/**
 * Interface for retrieving {@link CompetitionKeyStatisticsResource}
 */
public interface CompetitionKeyStatisticsRestService {
    RestResult<CompetitionKeyStatisticsResource> getKeyStatisticsByCompetition(long competitionId);
}
