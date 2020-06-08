package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

public interface CompetitionOrganisationConfigRestService {

    RestResult<CompetitionOrganisationConfigResource> findByCompetitionId(long competitionId);
}
