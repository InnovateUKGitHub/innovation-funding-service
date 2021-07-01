package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationExternalConfigResource;
import org.innovateuk.ifs.application.transactional.ApplicationExternalConfigService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationExternalConfigResourceBuilder.newApplicationExternalConfigResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationExternalConfigControllerTest extends BaseControllerMockMVCTest<ApplicationExternalConfigController> {

    @Mock
    private ApplicationExternalConfigService applicationExternalConfigServiceMock;


    @Override
    protected ApplicationExternalConfigController supplyControllerUnderTest() {
        return new ApplicationExternalConfigController();
    }

    @Test
    public void findOneByApplicationId() throws Exception {
        long applicationId = 100L;

       ApplicationExternalConfigResource resource = newApplicationExternalConfigResource().build();

        when(applicationExternalConfigServiceMock.findOneByApplicationId(applicationId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application-external-config/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(resource)));

        verify(applicationExternalConfigServiceMock).findOneByApplicationId(applicationId);
    }
}

