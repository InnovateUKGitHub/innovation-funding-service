package com.worth.ifs.application.service;

import org.springframework.core.io.ByteArrayResource;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

public interface ApplicationSummaryService {

	ApplicationSummaryPageResource findByCompetitionId(Long competitionId, int i, String sort);

	CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId);

	ApplicationSummaryPageResource getSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField);

	ApplicationSummaryPageResource getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField);

	ByteArrayResource downloadByCompetition(Long competitionId);
}
