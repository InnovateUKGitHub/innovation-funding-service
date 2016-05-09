package com.worth.ifs.application.transactional;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.SecuredBySpring;

/**
 * Transactional and secure service for making the decision of what applications to fund for a given competition.
 */
public interface ApplicationFundingService {

	@PreAuthorize("hasAuthority('comp_admin')")
	@SecuredBySpring(value = "MAKE_FUNDING_DECISION", securedType = FundingDecision.class, description = "Comp Admins should be able to make the decision of what applications to fund for a given competition")
	ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions);

}
