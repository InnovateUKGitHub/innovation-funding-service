package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

public interface ApplicationSummaryService {

	ApplicationSummaryPageResource findByCompetitionId(Long competitionId, int i, String sort);

	CompetitionSummaryResource getCompetitionSummaryByCompetitionId(Long competitionId);

	ClosedCompetitionApplicationSummaryPageResource getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageNumber, String sortField);

	ClosedCompetitionApplicationSummaryPageResource getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageNumber, String sortField);
}
