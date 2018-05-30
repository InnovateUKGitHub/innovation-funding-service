package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secure service for making the decision of what applications to fund for a given competition.
 */
public interface ApplicationNotificationTemplateService {

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "GET_DEFAULT_SUCCESSFUL_TEMPLATE", securedType = FundingDecision.class, description = "Comp Admins should be able to see default templates for application emails.")
	ServiceResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(long competitionId);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "GET_DEFAULT_UNSUCCESSFUL_TEMPLATE", securedType = FundingDecision.class, description = "Comp Admins should be able to see default templates for application emails.")
	ServiceResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(long competitionId);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "GET_DEFAULT_INELIGIBLE_TEMPLATE", securedType = FundingDecision.class, description = "Comp Admins should be able to see default templates for application emails.")
	ServiceResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(long competitionId);
}
