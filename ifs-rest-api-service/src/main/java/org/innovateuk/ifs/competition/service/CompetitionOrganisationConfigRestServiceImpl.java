package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompetitionOrganisationConfigRestServiceImpl extends BaseRestService implements CompetitionOrganisationConfigRestService {

    @Override
    public RestResult<Optional<CompetitionOrganisationConfigResource>> findByCompetitionId(long competitionId) {
        return getWithRestResultAnonymous("/competition-organisation-config/find-by-competition-id/" + competitionId, CompetitionOrganisationConfigResource.class).toOptionalIfNotFound();
    }
}
