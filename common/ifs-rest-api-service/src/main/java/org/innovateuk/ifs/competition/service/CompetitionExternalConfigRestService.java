package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;

public interface CompetitionExternalConfigRestService {
    RestResult<CompetitionExternalConfigResource> findOneByCompetitionId(long competitionId);
    RestResult<CompetitionExternalConfigResource> update(long competitionId, CompetitionExternalConfigResource competitionExternalConfigResource);
}
