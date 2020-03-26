package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

import java.util.Optional;

public interface CompetitionOrganisationConfigRestService {

    RestResult<Optional<CompetitionOrganisationConfigResource>> findByCompetitionId(long competitionId);
}
