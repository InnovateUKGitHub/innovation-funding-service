package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.*;

/**
 * Interface for retrieving {@link CompetitionKeyStatisticsResource}
 */
public interface CompetitionKeyStatisticsRestService {
    RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);
}
