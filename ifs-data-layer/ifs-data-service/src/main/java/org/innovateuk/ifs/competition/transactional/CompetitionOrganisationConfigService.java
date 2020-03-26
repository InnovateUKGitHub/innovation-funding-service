package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

import java.util.Optional;

public interface CompetitionOrganisationConfigService {

    @NotSecured(value = "Any user can find the international competitions", mustBeSecuredByOtherServices = false)
    ServiceResult<Optional<CompetitionOrganisationConfigResource>> findOneByCompetitionId(long competitionId);
}
