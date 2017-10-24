package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the action of retrieving application statistics
 */
public interface ApplicationCountSummaryRestService {
    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(long competitionId,
                                                                                                int pageIndex,
                                                                                                int pageSize,
                                                                                                String filter);

    RestResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionIdAndInnovationArea(long competitionId,
                                                                                                                 long assessorId,
                                                                                                                 int pageIndex,
                                                                                                                 int pageSize,
                                                                                                                 Optional<Long> innovationArea,
                                                                                                                 String filter,
                                                                                                                 String sortField);
}
