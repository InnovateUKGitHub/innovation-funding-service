package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;

/**
 * Interface for CRUD operations on {@link } related data.
 */
public interface CompetitionAssessmentConfigRestService {

    RestResult<CompetitionAssessmentConfigResource> findOneByCompetitionId(long competitionId);
    RestResult<Void> update(long competitionId, CompetitionAssessmentConfigResource competitionAssessmentConfigResource);
}
