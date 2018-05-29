package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationNotificationTemplateService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationNotificationTemplateControllerTest extends BaseControllerMockMVCTest<ApplicationNotificationTemplateController> {

    @Mock
    private ApplicationNotificationTemplateService applicationNotificationTemplateService;

    @Override
    protected ApplicationNotificationTemplateController supplyControllerUnderTest() {
        return new ApplicationNotificationTemplateController();
    }

    @Test
    public void getSuccessfulNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource();
        when(applicationNotificationTemplateService.getSuccessfulNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/successful/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(resource)));
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource();
        when(applicationNotificationTemplateService.getUnsuccessfulNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/unsuccessful/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(resource)));
    }

    @Test
    public void getIneligibleNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource();
        when(applicationNotificationTemplateService.getIneligibleNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/ineligible/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(resource)));
    }
}