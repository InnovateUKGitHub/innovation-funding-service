package com.worth.ifs.application.transactional;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;

public interface ApplicationFundingService {

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions);

}
