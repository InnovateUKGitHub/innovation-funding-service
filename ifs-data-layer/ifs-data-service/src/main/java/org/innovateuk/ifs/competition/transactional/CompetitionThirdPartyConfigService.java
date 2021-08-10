package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;

public interface CompetitionThirdPartyConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionThirdPartyConfigResource> findOneByCompetitionId(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionThirdPartyConfigResource> create(CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> update(long competitionId, CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource);
}
