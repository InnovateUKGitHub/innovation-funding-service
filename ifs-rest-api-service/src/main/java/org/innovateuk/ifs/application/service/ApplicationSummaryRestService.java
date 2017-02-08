package org.innovateuk.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize);
    
    RestResult<CompetitionSummaryResource> getCompetitionSummary(long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);
}
