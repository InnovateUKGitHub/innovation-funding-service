package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;

public interface CompetitionExternalConfigService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionExternalConfigResource> findOneByCompetitionId(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> update(long competitionId, CompetitionExternalConfigResource competitionExternalConfigResource);

}
