package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

public interface ApplicationFundingService {

	@PreAuthorize("hasAuthority('comp_admin')")
	ServiceResult<Void> makeFundingDecision(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions);

	@PreAuthorize("hasAuthority('comp_admin')")
	@SecuredBySpring(value = "SEND_FUNDING_DECISION_EMAILS", securedType = FundingDecision.class, description = "Comp Admins should be able to send emails to Lead Applicants confirming the Funding Panel's decisions on their Applications")
	ServiceResult<Void> notifyLeadApplicantsOfFundingDecisions(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions);
}
