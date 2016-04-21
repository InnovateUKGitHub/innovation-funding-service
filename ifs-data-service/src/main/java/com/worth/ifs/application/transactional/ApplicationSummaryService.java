package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAuthority('comp_admin')")
	@SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

	@PreAuthorize("hasAuthority('comp_admin')")
	@SecuredBySpring(value = "READ", description = "Comp Admins can see all submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);
	
	@PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all not-yet submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageIndex, String sortBy);

}
