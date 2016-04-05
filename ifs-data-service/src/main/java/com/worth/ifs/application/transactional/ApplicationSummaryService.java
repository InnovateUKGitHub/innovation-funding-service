package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ClosedCompetitionApplicationSummaryPageResource> getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageIndex, String sortBy);
	
	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ClosedCompetitionApplicationSummaryPageResource> getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);

	@PreAuthorize("hasAuthority('comp_admin')")
	List<Application> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Long applicationStatusId);
}
