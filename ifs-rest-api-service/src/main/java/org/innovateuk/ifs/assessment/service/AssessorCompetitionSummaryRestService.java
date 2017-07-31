package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * REST service that handles {@link AssessorCompetitionSummaryResource}s.
 */
public interface AssessorCompetitionSummaryRestService {

    RestResult<AssessorCompetitionSummaryResource> getAssessorSummary(long assessorId, long competitionId);
}
