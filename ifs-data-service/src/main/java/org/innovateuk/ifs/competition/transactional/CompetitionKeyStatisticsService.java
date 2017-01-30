package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionKeyStatisticsResource;

public interface CompetitionKeyStatisticsService {
    ServiceResult<CompetitionKeyStatisticsResource> getKeyStatisticsByCompetition(long competitionId);
}
