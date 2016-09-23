package com.worth.ifs.application.transactional;

import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public interface CompetitionSummaryService {
	
	@PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
	@SecuredBySpring(value = "VIEW", securedType = CompetitionSummaryResource.class,
			description = "Comp Admins and Project Finance team members can see Competition Summaries")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);
}
