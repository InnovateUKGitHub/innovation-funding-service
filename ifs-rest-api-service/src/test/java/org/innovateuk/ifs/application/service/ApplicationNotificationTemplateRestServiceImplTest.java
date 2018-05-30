package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

public class ApplicationNotificationTemplateRestServiceImplTest extends BaseRestServiceUnitTest<ApplicationNotificationTemplateRestServiceImpl> {
    private String baseUrl = "/application-notification-template";

    @Override
    protected ApplicationNotificationTemplateRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationNotificationTemplateRestServiceImpl();
    }

    @Test
    public void getSuccessfulNotificationTemplate() {
        Long competitionId = 123L;
        String expectedUrl = baseUrl + "/successful/" + competitionId;

        ApplicationNotificationTemplateResource applicationNotificationTemplateResource = new ApplicationNotificationTemplateResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationNotificationTemplateResource.class, applicationNotificationTemplateResource);

        RestResult<ApplicationNotificationTemplateResource> result = service.getSuccessfulNotificationTemplate(competitionId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(applicationNotificationTemplateResource, result.getSuccess());
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() {
        Long competitionId = 123L;
        String expectedUrl = baseUrl + "/unsuccessful/" + competitionId;

        ApplicationNotificationTemplateResource applicationNotificationTemplateResource = new ApplicationNotificationTemplateResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationNotificationTemplateResource.class, applicationNotificationTemplateResource);

        RestResult<ApplicationNotificationTemplateResource> result = service.getUnsuccessfulNotificationTemplate(competitionId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(applicationNotificationTemplateResource, result.getSuccess());
    }

    @Test
    public void getIneligibleNotificationTemplate() {
        long competitionId = 1L;
        String expectedUrl = baseUrl + "/ineligible/" + competitionId;

        ApplicationNotificationTemplateResource applicationNotificationTemplateResource = new ApplicationNotificationTemplateResource();

        setupGetWithRestResultExpectations(expectedUrl, ApplicationNotificationTemplateResource.class, applicationNotificationTemplateResource);

        RestResult<ApplicationNotificationTemplateResource> result = service.getIneligibleNotificationTemplate(competitionId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(applicationNotificationTemplateResource, result.getSuccess());
    }
}
