package org.innovateuk.ifs.virtualassistant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantAuthRestClient.AZURE_CHAT_BOT_REST_TEMPLATE_QUALIFIER;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantAuthRestClientTest.*;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantController.THYMELEAF_MAPPING;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantModel.NO_REMOTE_SERVER_MSG;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantTestHelper.assertVirtualAssistantModel;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {VirtualAssistantController.class, VirtualAssistantAuthRestClient.class})
@TestPropertySource(properties = {
        "ifs.web.virtualAssistant.botSecret=" + TEST_BOT_SECRET,
        "ifs.web.virtualAssistant.botId=" + TEST_BOT_ID,
        "ifs.web.virtualAssistant.tokenExchangeUrl=" + TEST_URL,
})
public class VirtualAssistantComponentTest {

    @Autowired
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient;

    @Autowired
    private VirtualAssistantController virtualAssistantController;

    @MockBean
    @Qualifier(AZURE_CHAT_BOT_REST_TEMPLATE_QUALIFIER)
    private RestTemplate azureChatBotRestTemplate;

    @Test
    public void testObtainAuthToken() {
        Model model = new ExtendedModelMap();
        when(azureChatBotRestTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(FAKE_AUTH_STRING_JSON, HttpStatus.OK));

        assertThat(virtualAssistantController.virtualAssistant(model), equalTo(THYMELEAF_MAPPING));
        assertVirtualAssistantModel(model, TEST_BOT_ID, FAKE_AUTH_STRING, "", true);
    }

    @Test
    public void testObtainAuthTokenFailedAuth() {
        Model model = new ExtendedModelMap();
        when(azureChatBotRestTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        assertThat(virtualAssistantController.virtualAssistant(model), equalTo(THYMELEAF_MAPPING));
        assertVirtualAssistantModel(model, NO_REMOTE_SERVER_MSG,
                NO_REMOTE_SERVER_MSG, HttpStatus.UNAUTHORIZED.toString(), false);
    }

    @Test
    public void testObtainAuthTokenClientError() {
        Model model = new ExtendedModelMap();
        when(azureChatBotRestTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        assertThat(virtualAssistantController.virtualAssistant(model), equalTo(THYMELEAF_MAPPING));
        assertVirtualAssistantModel(model, NO_REMOTE_SERVER_MSG, NO_REMOTE_SERVER_MSG,
                HttpStatus.BAD_GATEWAY.value() + " " + HttpStatus.BAD_GATEWAY.name(), false);
    }

}
