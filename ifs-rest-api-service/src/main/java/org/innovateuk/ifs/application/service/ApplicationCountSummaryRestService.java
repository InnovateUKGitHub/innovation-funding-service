package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface ApplicationCountSummaryRestService {
    RestResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId);
}
