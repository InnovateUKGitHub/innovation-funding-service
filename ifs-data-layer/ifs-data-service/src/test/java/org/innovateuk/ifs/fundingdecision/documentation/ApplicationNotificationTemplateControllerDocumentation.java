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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

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
        			.contentType(MediaType.APPLICATION_JSON))
        		.andDo(document("application-notification-template/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Competition ID to get the template of.")
                        ),
                        responseFields(
                                fieldWithPath("messageBody").description("The body of the template"))
                        )
                );
    }

    @Test
    public void getUnsuccessfulNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource("Content");

        when(applicationNotificationTemplateService.getUnsuccessfulNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/unsuccessful/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("application-notification-template/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Competition ID to get the template of.")
                        ),
                        responseFields(
                                fieldWithPath("messageBody").description("The body of the template"))
                        )
                );
    }

    @Test
    public void getIneligibleNotificationTemplate() throws Exception {
        Long competitionId = 1L;
        long userId = 2L;
        ApplicationNotificationTemplateResource resource = new ApplicationNotificationTemplateResource("Content");

        when(applicationNotificationTemplateService.getIneligibleNotificationTemplate(competitionId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-notification-template/ineligible/{competitionId}", competitionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("application-notification-template/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Competition ID to get the template of.")
                        ),
                        responseFields(
                                fieldWithPath("messageBody").description("The body of the template"))
                        )
                );
    }

}
