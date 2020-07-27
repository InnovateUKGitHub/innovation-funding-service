package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;

public interface CompetitionApplicationConfigRestService {
    RestResult<CompetitionApplicationConfigResource> findOneByCompetitionId(long competitionId);
}
