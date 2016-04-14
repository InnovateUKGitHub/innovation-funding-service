package com.worth.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.rest.RestResult;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, int pageNumber, String sortField);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField);

    RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField);

    RestResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);
}
