package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionAverageAssessorScoreConfigResource;

public interface CompetitionAverageAssessorScoreConfigService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionAverageAssessorScoreConfigResource> findOneByCompetitionId(long competitionId);
}
