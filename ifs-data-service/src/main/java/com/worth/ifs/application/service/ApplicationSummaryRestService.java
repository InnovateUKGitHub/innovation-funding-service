package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.core.io.ByteArrayResource;

public interface ApplicationSummaryRestService {

    public RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, int pageNumber, String sortField);

    public RestResult<ApplicationSummaryResource> getApplicationSummary(Long id);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);
}
