package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;

/**
 * Interface for CRUD operations on {@link AssessmentPeriodResource} related data.
 */
public interface AssessmentPeriodRestService {

    RestResult<AssessmentPeriodResource> getAssessmentPeriodByCompetitionIdAndIndex(Integer index, Long competitionId);

    RestResult<AssessmentPeriodResource> create(Integer index, Long competitionId);
}
