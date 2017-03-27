package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;

import java.util.Optional;

@Service
public class ApplicationSummaryServiceImpl implements ApplicationSummaryService {

	@Autowired
	private ApplicationSummaryRestService applicationSummaryRestService;
	
	@Override
	public ApplicationSummaryPageResource findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		return applicationSummaryRestService.getAllApplications(competitionId, sortField, pageNumber, pageSize, filter).getSuccessObjectOrThrowException();
	}

	@Override
	public CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId) {
		return applicationSummaryRestService.getCompetitionSummary(competitionId).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter, Optional<FundingDecision> fundingFilter) {
		return applicationSummaryRestService.getSubmittedApplications(competitionId, sortField, pageNumber, pageSize, filter, fundingFilter).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		return applicationSummaryRestService.getNonSubmittedApplications(competitionId, sortField, pageNumber, pageSize, filter).getSuccessObjectOrThrowException();
	}
	
	@Override
	public ApplicationSummaryPageResource getApplicationsRequiringFeedbackByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		return applicationSummaryRestService.getFeedbackRequiredApplications(competitionId, sortField, pageNumber, pageSize, filter).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getWithFundingDecisionApplications(Long competitionId,
																			 String sortField,
																			 Integer pageNumber,
																			 Integer pageSize,
																			 String filter,
																			 Optional<Boolean> sendFilter,
																			 Optional<FundingDecision> fundingFilter) {
		return applicationSummaryRestService.getWithFundingDecisionApplications(competitionId, sortField, pageNumber, pageSize, filter,sendFilter, fundingFilter).getSuccessObjectOrThrowException();
	}

	@Override
	public Long getApplicationsRequiringFeedbackCountByCompetitionId(Long competitionId) {
		ApplicationSummaryPageResource page = applicationSummaryRestService.getFeedbackRequiredApplications(competitionId, null, 0, 1, null).getSuccessObjectOrThrowException();
		return page.getTotalElements();
	}
	
	@Override
	public ByteArrayResource downloadByCompetition(Long competitionId) {
		return applicationSummaryRestService.downloadByCompetition(competitionId).getSuccessObjectOrThrowException();
	}

}
