package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.error.ErrorHolder;
import org.innovateuk.ifs.commons.service.BaseEitherBackedResult;
import org.springframework.core.io.ByteArrayResource;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.Optional;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter, Optional<FundingDecision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter);

    RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter);
    
    RestResult<CompetitionSummaryResource> getCompetitionSummary(long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);

    RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplications(Long competitionId,
                                                                                  String sortField,
                                                                                  int pageNumber,
                                                                                  int pageSize,
                                                                                  String filter,
                                                                                  Optional<Boolean> sendFilter,
                                                                                  Optional<FundingDecision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getIneligibleApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter);
}
