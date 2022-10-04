package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.springframework.transaction.annotation.Transactional;

public interface CompetitionApplicationConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionApplicationConfigResource> findOneByCompetitionId(long competitionId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> update(long competitionId, CompetitionApplicationConfigResource competitionApplicationConfigResource);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateImpactSurvey(long competitionId, CompetitionApplicationConfigResource competitionApplicationConfigResource);
}
