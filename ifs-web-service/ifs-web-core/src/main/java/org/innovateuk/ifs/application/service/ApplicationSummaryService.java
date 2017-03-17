package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.springframework.core.io.ByteArrayResource;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;

import java.util.Optional;

public interface ApplicationSummaryService {

	ApplicationSummaryPageResource findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter);

	CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId);

	ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter, Optional<FundingDecision> fundingFilter);

	ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter);

	ApplicationSummaryPageResource getApplicationsRequiringFeedbackByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter);

	ApplicationSummaryPageResource getWithFundingDecisionApplications(Long competitionId,
																	  String sortField,
																	  Integer pageNumber,
																	  Integer pageSize,
																	  String filter,
																	  Optional<Boolean> sendFilter,
																	  Optional<FundingDecision> fundingFilter);

	Long getApplicationsRequiringFeedbackCountByCompetitionId(Long competitionId);

	ByteArrayResource downloadByCompetition(Long competitionId);
}
