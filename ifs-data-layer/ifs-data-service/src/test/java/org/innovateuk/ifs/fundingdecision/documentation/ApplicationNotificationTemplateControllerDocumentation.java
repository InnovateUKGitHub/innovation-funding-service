package org.innovateuk.ifs.fundingdecision.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.fundingdecision.controller.ApplicationNotificationTemplateController;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationNotificationTemplateService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ApplicationNotificationTemplateControllerDocumentation extends BaseControllerMockMVCTest<ApplicationNotificationTemplateController> {

    @Mock
    private ApplicationNotificationTemplateService applicationNotificationTemplateService;

    @Override
    protected ApplicationNotificationTemplateController supplyControllerUnderTest() {
        return new ApplicationNotificationTemplateController();
    }

    @Test
    public void getSuccessfulNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource("Content");

        when(applicationNotificationTemplateService.getSuccessfulNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/successful/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource("Content");

        when(applicationNotificationTemplateService.getUnsuccessfulNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/unsuccessful/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getIneligibleNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        long userId = 2L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource("Content");

        when(applicationNotificationTemplateService.getIneligibleNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/ineligible/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

}
