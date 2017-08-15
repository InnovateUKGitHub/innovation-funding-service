package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.Optional;

public interface AssessorCountSummaryRestService {

    RestResult<AssessorCountSummaryPageResource> getAssessorCountSummariesByCompetitionId(long competitionId, Optional<Long> innovationSectorId, Optional<BusinessType> businessType, Integer pageIndex, Integer pageSize);
}