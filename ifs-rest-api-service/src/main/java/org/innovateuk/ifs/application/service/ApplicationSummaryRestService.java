package org.innovateuk.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> getAllApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

    RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);
    
    RestResult<CompetitionSummaryResource> getCompetitionSummary(Long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);
}
