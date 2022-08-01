package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
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
	@SecuredBySpring(value = "SEND_FUNDING_DECISION_EMAILS", securedType = Decision.class, description = "Comp Admins should be able to send emails to Lead Applicants confirming the Funding Panel's decisions on their Applications")
	ServiceResult<Void> notifyApplicantsOfDecisions(FundingNotificationResource fundingNotificationResource);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "SAVE_FUNDING_DECISION_DATA", securedType = Decision.class, description = "Comp Admins should be able to save the decision of what applications to fund for a given competition")
	ServiceResult<Void> saveDecisionData(Long competitionId, Map<Long, Decision> applicationDecisions);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "GET_FUNDING_DECISION_EMAIL_TARGETS", securedType = Decision.class, description = "Comp Admins should be able to send emails to Lead Applicants confirming the Funding Panel's decisions on their Applications")
	ServiceResult<List<ApplicationDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds);

}
