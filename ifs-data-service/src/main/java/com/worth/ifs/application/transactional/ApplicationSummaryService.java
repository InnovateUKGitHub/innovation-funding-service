package com.worth.ifs.application.transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<ApplicationSummaryResource> getApplicationSummaryById(Long id);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

}
