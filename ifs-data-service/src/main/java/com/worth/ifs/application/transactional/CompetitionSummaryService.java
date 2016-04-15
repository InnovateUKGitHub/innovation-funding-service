package com.worth.ifs.application.transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;

public interface CompetitionSummaryService {
	
	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId);
}
