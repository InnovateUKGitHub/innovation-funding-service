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
                                                                        Optional<Decision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getSubmittedEoiApplications(long competitionId,
                                                                           String sortField,
                                                                           int pageNumber,
                                                                           int pageSize,
                                                                           Optional<String> filter,
                                                                           Optional<Decision> fundingFilter,
                                                                           Optional<Boolean> sendFilter);

    RestResult<ApplicationSummaryPageResource> getSubmittedApplicationsWithPanelStatus(long competitionId,
                                                                        String sortField,
                                                                        int pageNumber,
                                                                        int pageSize,
                                                                        Optional<String> filter,
                                                                        Optional<Decision> fundingFilter,
                                                                        Optional<Boolean> inAssessmentReviewPanel);

    RestResult<List<Long>> getAllSubmittedApplicationIds(long competitionId,
                                                         Optional<String> filter,
                                                         Optional<Decision> fundingFilter);

    RestResult<List<Long>> getAllSubmittedEoiApplicationIds(long competitionId,
                                                            Optional<String> filter,
                                                            Optional<Decision> fundingFilter,
                                                            Optional<Boolean> sendFilter);

    RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField,
                                                                           int pageNumber,
                                                                           int pageSize,
                                                                           Optional<String> filter);

    RestResult<CompetitionSummaryResource> getCompetitionSummary(long competitionId);

    RestResult<ByteArrayResource> downloadByCompetition(long competitionId);

    RestResult<ApplicationSummaryPageResource> getWithDecisionApplications(Long competitionId,
                                                                                  String sortField,
                                                                                  int pageNumber,
                                                                                  int pageSize,
                                                                                  Optional<String> filter,
                                                                                  Optional<Boolean> sendFilter,
                                                                                  Optional<Decision> fundingFilter,
                                                                                  Optional<Boolean> eoiFilter);

    RestResult<List<Long>> getWithDecisionIsChangeableApplicationIdsByCompetitionId(Long competitionId,
                                                                                           Optional<String> filter,
                                                                                           Optional<Boolean> sendFilter,
                                                                                           Optional<Decision> fundingFilter,
                                                                                           Optional<Boolean> eoiFilter);

    RestResult<ApplicationSummaryPageResource> getIneligibleApplications(long competitionId,
                                                                         String sortField,
                                                                         int pageNumber,
                                                                         int pageSize,
                                                                         Optional<String> filter,
                                                                         Optional<Boolean> informFilter);

    RestResult<List<PreviousApplicationResource>> getPreviousApplications(long competitionId);

    RestResult<List<Long>> getAllAssessedApplicationIds(long competitionId,
                                                         Optional<String> filter,
                                                         Optional<Decision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getAssessedApplications(long competitionId,
                                                                        String sortField,
                                                                        int pageNumber,
                                                                        int pageSize,
                                                                        Optional<String> filter,
                                                                        Optional<Decision> fundingFilter);

    RestResult<ApplicationSummaryPageResource> getAssessedApplicationsWithPanelStatus(long competitionId,
                                                                                       String sortField,
                                                                                       int pageNumber,
                                                                                       int pageSize,
                                                                                       Optional<String> filter,
                                                                                       Optional<Decision> fundingFilter,
                                                                                       Optional<Boolean> inAssessmentReviewPanel);

}
