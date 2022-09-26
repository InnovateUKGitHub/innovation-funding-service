package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;

public interface CompetitionEoiEvidenceConfigRestService {

    RestResult<CompetitionEoiEvidenceConfigResource> findOneByCompetitionId(long competitionId);
}
