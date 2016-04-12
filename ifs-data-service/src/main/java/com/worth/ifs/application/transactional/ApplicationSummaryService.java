package com.worth.ifs.application.transactional;

import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ClosedCompetitionSubmittedApplicationSummaryPageResource> getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageIndex, String sortBy);
	
	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ClosedCompetitionNotSubmittedApplicationSummaryPageResource> getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<List<Application>> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Collection<Long> applicationStatusId);

}
