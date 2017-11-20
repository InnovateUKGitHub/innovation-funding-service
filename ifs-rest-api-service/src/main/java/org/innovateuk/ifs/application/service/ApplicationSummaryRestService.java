package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Optional;

public interface ApplicationSummaryRestService {

    RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId,
                                                                  String sortField,
                                                                  int pageNumber,
                                                                  int pageSize,
                                                                  Optional<String> filter);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId,
                                                                        String sortField,
                                                                        int pageNumber,
                                                                        int pageSize,
                                                                        Optional<String> filter,
                                                                        Optional<FundingDecision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplicationsWithPanelStatus(long competitionId,
                                                                        String sortField,
                                                                        int pageNumber,
                                                                        int pageSize,
                                                                        Optional<String> filter,
                                                                        Optional<FundingDecision> fundingFilter,
                                                                        Optional<Boolean> inAssessmentPanel);


    RestResult<List<Long>> getAllSubmittedApplicationIds(long competitionId,
                                                         Optional<String> filter,
                                                         Optional<FundingDecision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField,
                                                                           int pageNumber,
                                                                           int pageSize,
                                                                           Optional<String> filter);

    RestResult<CompetitionSummaryResource> getCompetitionSummary(long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);

    RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplications(Long competitionId,
                                                                                  String sortField,
                                                                                  int pageNumber,
                                                                                  int pageSize,
                                                                                  Optional<String> filter,
                                                                                  Optional<Boolean> sendFilter,
                                                                                  Optional<FundingDecision> fundingFilter);

    RestResult<List<Long>> getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(Long competitionId,
                                                                                           Optional<String> filter,
                                                                                           Optional<Boolean> sendFilter,
                                                                                           Optional<FundingDecision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getIneligibleApplications(long competitionId,
                                                                         String sortField,
                                                                         int pageNumber,
                                                                         int pageSize,
                                                                         Optional<String> filter,
                                                                         Optional<Boolean> informFilter);

    RestResult<ApplicationTeamResource> getApplicationTeam(long applicationId);
}
