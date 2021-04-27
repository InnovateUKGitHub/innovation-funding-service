package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for the action of retrieving application statistics
 */
public interface ApplicationCountSummaryRestService {
    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                int pageIndex,
                                                                                                int pageSize,
                                                                                                String filter);

    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorIdAndAssessmentPeriodId(long competitionId,
                                                                                                                                  long assessorId,
                                                                                                                                  long assessmentPeriodId,
                                                                                                                                  int page,
                                                                                                                                  Sort sort,
                                                                                                                                  String filter);

    RestResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(long competitionId,
                                                                         long assessorId,
                                                                         String filter);
}
