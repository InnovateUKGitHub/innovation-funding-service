package com.worth.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.rest.RestResult;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);
    
    RestResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);
}
