package com.worth.ifs.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

@Service
public class ApplicationSummaryServiceImpl implements ApplicationSummaryService {

	@Autowired
	private ApplicationSummaryRestService applicationSummaryRestService;
	
	@Override
	public ApplicationSummaryPageResource findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		return applicationSummaryRestService.findByCompetitionId(competitionId, sortField, pageNumber, pageSize).getSuccessObjectOrThrowException();
	}

	@Override
	public CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId) {
		return applicationSummaryRestService.getCompetitionSummaryByCompetitionId(competitionId).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		return applicationSummaryRestService.getSubmittedApplicationSummariesByCompetitionId(competitionId, sortField, pageNumber, pageSize).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		return applicationSummaryRestService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, sortField, pageNumber, pageSize).getSuccessObjectOrThrowException();
	}
	
	@Override
	public ApplicationSummaryPageResource getApplicationsRequiringFeedbackByCompetitionId(
			Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		return applicationSummaryRestService.getFeedbackRequiredApplicationSummariesByCompetitionId(competitionId, sortField, pageNumber, pageSize).getSuccessObjectOrThrowException();
	}
	
	@Override
	public Long getApplicationsRequiringFeedbackCountByCompetitionId(Long competitionId) {
		ApplicationSummaryPageResource page = applicationSummaryRestService.getFeedbackRequiredApplicationSummariesByCompetitionId(competitionId, null, 0, 1).getSuccessObjectOrThrowException();
		return page.getTotalElements();
	}
	
	@Override
	public ByteArrayResource downloadByCompetition(Long competitionId) {
		return applicationSummaryRestService.downloadByCompetition(competitionId).getSuccessObjectOrThrowException();
	}

}
