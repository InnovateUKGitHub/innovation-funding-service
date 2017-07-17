package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface AssessorCountSummaryRestService {

    RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Integer pageIndex, Integer pageSize);
}