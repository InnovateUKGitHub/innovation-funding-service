package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;

public interface CompetitionThirdPartyConfigRestService {
    RestResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(long competitionId);
    RestResult<Void> update(long competitionId, CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource);
}
