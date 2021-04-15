package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface AssessorCountSummaryRestService {

    RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(
            long competitionId, String assessorNameFilter, Integer pageIndex, Integer pageSize);

    RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionIdAndAssessmentPeriodId(
            long competitionId, long assessmentPeriodId, String assessorNameFilter, Integer pageIndex, Integer pageSize);
}