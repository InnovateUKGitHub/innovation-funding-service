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
	public ApplicationSummaryPageResource findByCompetitionId(Long competitionId, int i, String sort) {
		return applicationSummaryRestService.findByCompetitionId(competitionId, i, sort).getSuccessObjectOrThrowException();
	}

	@Override
	public CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId) {
		return applicationSummaryRestService.getCompetitionSummaryByCompetitionId(competitionId).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, int pageNumber, String sortField) {
		return applicationSummaryRestService.getSubmittedApplicationSummariesByCompetitionId(competitionId, pageNumber, sortField).getSuccessObjectOrThrowException();
	}

	@Override
	public ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(
			Long competitionId, int pageNumber, String sortField) {
		return applicationSummaryRestService.getNotSubmittedApplicationSummariesByCompetitionId(competitionId, pageNumber, sortField).getSuccessObjectOrThrowException();
	}

	@Override
	public ByteArrayResource downloadByCompetition(Long competitionId) {
		return applicationSummaryRestService.downloadByCompetition(competitionId).getSuccessObjectOrThrowException();
	}

}
