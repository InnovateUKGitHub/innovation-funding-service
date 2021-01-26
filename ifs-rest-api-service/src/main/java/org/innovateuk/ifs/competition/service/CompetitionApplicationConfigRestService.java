package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;

public interface CompetitionApplicationConfigRestService {
    RestResult<CompetitionApplicationConfigResource> findOneByCompetitionId(long competitionId);
}
