package org.innovateuk.ifs.fundingdecision.transactional;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Transactional and secure service for making the decision of what applications to fund for a given competition.
 */
public interface ApplicationNotificationTemplateService {

	ServiceResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(long competitionId);

	ServiceResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(long competitionId);

	ServiceResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(long competitionId);
}
