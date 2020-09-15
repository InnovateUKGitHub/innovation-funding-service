package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Transactional and secure service for making the decision of what applications to fund for a given competition.
 */
public interface ApplicationFundingService {

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "SEND_FUNDING_DECISION_EMAILS", securedType = FundingDecision.class, description = "Comp Admins should be able to send emails to Lead Applicants confirming the Funding Panel's decisions on their Applications")
	ServiceResult<Void> notifyApplicantsOfFundingDecisions(FundingNotificationResource fundingNotificationResource);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "SAVE_FUNDING_DECISION_DATA", securedType = FundingDecision.class, description = "Comp Admins should be able to save the decision of what applications to fund for a given competition")
	ServiceResult<Void> saveFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationFundingDecisions);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "GET_FUNDING_DECISION_EMAIL_TARGETS", securedType = FundingDecision.class, description = "Comp Admins should be able to send emails to Lead Applicants confirming the Funding Panel's decisions on their Applications")
	ServiceResult<List<FundingDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds);
}
