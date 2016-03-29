package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.rest.RestResult;

public interface ApplicationSummaryRestService {

    public RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, int pageNumber);

    public RestResult<ApplicationSummaryResource> getApplicationSummary(Long id);
}
