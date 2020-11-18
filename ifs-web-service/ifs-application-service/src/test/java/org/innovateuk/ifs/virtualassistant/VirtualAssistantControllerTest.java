package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc.perform(get("/virtual-assistant"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("virtualAssistantBotId", "botIdValue"))
                .andExpect(model().attribute("virtualAssistantClientToken", "clientTokenValue"))
                .andExpect(model().attribute("virtualAssistantErrorMessage", ""))
                .andExpect(model().attribute("virtualAssistantServerAvailable", true))
                .andExpect(view().name("virtual-assistant"));
    }

    @Test
    public void testAuthFailure() throws Exception {
        when(virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails())
                .thenReturn(new VirtualAssistantModel("errorMessageValue"));
        mockMvc.perform(get("/virtual-assistant"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("virtualAssistantBotId", "noRemoteServer"))
                .andExpect(model().attribute("virtualAssistantClientToken", "noRemoteServer"))
                .andExpect(model().attribute("virtualAssistantErrorMessage", "errorMessageValue"))
                .andExpect(model().attribute("virtualAssistantServerAvailable", false))
                .andExpect(view().name("virtual-assistant"));
    }

}