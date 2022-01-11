package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.crud.IfsCrudRestService;

import java.util.List;

/**
 * Interface for CRUD operations on {@link AssessmentPeriodResource} related data.
 */
public interface AssessmentPeriodRestService extends IfsCrudRestService<AssessmentPeriodResource, Long> {
    RestResult<List<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId);

    RestResult<PageResource<AssessmentPeriodResource>> getAssessmentPeriodByCompetitionId(long competitionId, int page, int size);
}
