package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface CompetitionSummaryService {
	
	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'competition_technologist')")
	@SecuredBySpring(value = "VIEW", securedType = CompetitionSummaryResource.class,
			description = "Internal users can see Competition Summaries")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);
}
