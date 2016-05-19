package com.worth.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

public interface ApplicationSummaryService {

	ApplicationSummaryPageResource findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

	CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId);

	ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

	ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);

	ApplicationSummaryPageResource getFundedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize);
	
	Long getFundedApplicationCountByCompetitionId(Long competitionId);
	
	Long getApplicationsRequiringFeedbackCountByCompetitionId(Long competitionId);

	ByteArrayResource downloadByCompetition(Long competitionId);
}
