package org.innovateuk.ifs.virtualassistant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantAuthRestClientTest.*;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantController.*;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantModel.NO_REMOTE_SERVER_MSG;
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
    private RestTemplate restTemplate;

    @Test
    public void testObtainAuthToken() {
        Model model = new ExtendedModelMap();
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(FAKE_AUTH_STRING_JSON, HttpStatus.OK));
        String thymeleafMapping = virtualAssistantController.virtualAssistant(model);

        assertThat(thymeleafMapping, equalTo(THYMELEAF_MAPPING));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_BOT_ID), equalTo(TEST_BOT_ID));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_CLIENT_TOKEN), equalTo(FAKE_AUTH_STRING));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_ERROR_MESSAGE), equalTo(""));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_SERVER_AVAILABLE), equalTo(true));
    }

    @Test
    public void testObtainAuthTokenFailedAuth() {
        Model model = new ExtendedModelMap();
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        String thymeleafMapping = virtualAssistantController.virtualAssistant(model);

        assertThat(thymeleafMapping, equalTo(THYMELEAF_MAPPING));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_BOT_ID), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_CLIENT_TOKEN), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_ERROR_MESSAGE), equalTo(HttpStatus.UNAUTHORIZED.toString()));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_SERVER_AVAILABLE), equalTo(false));
    }

    @Test
    public void testObtainAuthTokenClientError() {
        Model model = new ExtendedModelMap();
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        String thymeleafMapping = virtualAssistantController.virtualAssistant(model);

        assertThat(thymeleafMapping, equalTo(THYMELEAF_MAPPING));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_BOT_ID), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_CLIENT_TOKEN), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_ERROR_MESSAGE),
                equalTo(HttpStatus.BAD_GATEWAY.value() + " " + HttpStatus.BAD_GATEWAY.name()));
        assertThat(model.asMap().get(VIRTUAL_ASSISTANT_SERVER_AVAILABLE), equalTo(false));
    }

}
