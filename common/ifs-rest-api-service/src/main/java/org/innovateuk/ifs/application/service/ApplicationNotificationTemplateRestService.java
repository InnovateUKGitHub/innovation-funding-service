package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface ApplicationNotificationTemplateRestService {

    RestResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(long competitionId);
    RestResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(long competitionId);
    RestResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(long competitionId);
}
