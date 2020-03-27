package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;

public interface CompetitionOrganisationConfigService {

    @NotSecured(value = "Any user can find the international competitions", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionOrganisationConfigResource> findOneByCompetitionId(long competitionId);
}
