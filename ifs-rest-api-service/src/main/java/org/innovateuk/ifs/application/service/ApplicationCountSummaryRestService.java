package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for the action of retrieving application statistics
 */
public interface ApplicationCountSummaryRestService {

    @ZeroDowntime(reference = "IFS-8853", description = "This can probably be removed")
    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                int pageIndex,
                                                                                                int pageSize,
                                                                                                String filter);

    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessmentPeriodId(long competitionId,
                                                                                                                     long assessmentPeriodId,
                                                                                                                     int pageIndex,
                                                                                                                     int pageSize,
                                                                                                                     String filter);

    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndAssessorId(long competitionId,
                                                                                                              long assessorId,
                                                                                                              int page,
                                                                                                              Sort sort,
                                                                                                              String filter);

    RestResult<List<Long>> getApplicationIdsByCompetitionIdAndAssessorId(long competitionId,
                                                                         long assessorId,
                                                                         String filter);
}
