package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

/**
 * Service implements methods for setting an Applications innovation area and retrieving available choices.
 */
@Service
public class ApplicationNotificationTemplateRestServiceImpl extends BaseRestService implements ApplicationNotificationTemplateRestService {
    private String baseUrl = "/application-notification-template";

    @Override
    public RestResult<ApplicationNotificationTemplateResource> getSuccessfulNotificationTemplate(long competitionId) {
        return getWithRestResult(String.format("%s/%s/%s", baseUrl, "successful", competitionId), ApplicationNotificationTemplateResource.class);
    }

    @Override
    public RestResult<ApplicationNotificationTemplateResource> getUnsuccessfulNotificationTemplate(long competitionId) {
        return getWithRestResult(String.format("%s/%s/%s", baseUrl, "unsuccessful", competitionId), ApplicationNotificationTemplateResource.class);
    }

    @Override
    public RestResult<ApplicationNotificationTemplateResource> getIneligibleNotificationTemplate(long competitionId) {
        return getWithRestResult(String.format("%s/%s/%s", baseUrl, "ineligible", competitionId), ApplicationNotificationTemplateResource.class);
    }
}
