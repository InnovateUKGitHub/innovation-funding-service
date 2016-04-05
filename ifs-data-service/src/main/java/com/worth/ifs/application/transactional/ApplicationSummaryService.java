package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<ApplicationSummaryResource> getApplicationSummaryById(Long id);

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	List<Application> getApplicationSummariesByCompetitionIdAndStatus(Long competitionId, Long applicationStatusId);
}
