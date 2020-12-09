package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.virtualassistant.VirtualAssistantController.THYMELEAF_MAPPING;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantModel.NO_REMOTE_SERVER_MSG;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantTestHelper.assertVirtualAssistantModel;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class VirtualAssistantControllerTest extends BaseControllerMockMVCTest<VirtualAssistantController> {

    @Mock
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient;

    @Override
    protected VirtualAssistantController supplyControllerUnderTest() {
        return new VirtualAssistantController();
    }

    @Test
    public void testAuthSuccess() throws Exception {
        when(virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails())
                .thenReturn(new VirtualAssistantModel("botIdValue", "clientTokenValue"));
        mockMvc.perform(get(VirtualAssistantController.REQUEST_MAPPING))
                .andExpect(status().is2xxSuccessful())
                .andExpect(assertVirtualAssistantModel("botIdValue",
                        "clientTokenValue", "", true))
                .andExpect(view().name(THYMELEAF_MAPPING));
    }

    @Test
    public void testAuthFailure() throws Exception {
        when(virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails())
                .thenReturn(new VirtualAssistantModel("errorMessageValue"));
        mockMvc.perform(get(VirtualAssistantController.REQUEST_MAPPING))
                .andExpect(status().is2xxSuccessful())
                .andExpect(assertVirtualAssistantModel(NO_REMOTE_SERVER_MSG,
                        NO_REMOTE_SERVER_MSG, "errorMessageValue", false))
                .andExpect(view().name(THYMELEAF_MAPPING));
    }

}