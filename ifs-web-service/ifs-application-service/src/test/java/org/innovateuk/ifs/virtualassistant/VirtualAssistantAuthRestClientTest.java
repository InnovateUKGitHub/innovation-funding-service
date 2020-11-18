package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VirtualAssistantAuthRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient = new VirtualAssistantAuthRestClient();

    private static final String TEST_URL = "http://test";
    private static final String TEST_BOT_ID = "testBot";
    private static final String TEST_BOT_SECRET = "testBotSecret";

    @Before
    public void init() {
        ReflectionTestUtils.setField(virtualAssistantAuthRestClient, "tokenExchangeUrl", TEST_URL);
        ReflectionTestUtils.setField(virtualAssistantAuthRestClient, "botId", TEST_BOT_ID);
        ReflectionTestUtils.setField(virtualAssistantAuthRestClient, "botSecret", TEST_BOT_SECRET);
    }

    @Test
    public void testObtainVirtualAssistantAuthDetails() {
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
                new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>("thiswouldbeajsonauthstring", HttpStatus.OK));
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(true));
        assertThat(virtualAssistantModel.getBotId(), equalTo(TEST_BOT_ID));
        assertThat(virtualAssistantModel.getClientToken(),
                equalTo(JsonUtil.getObjectFromJson("thiswouldbeajsonauthstring", String.class)));
    }

    @Test
    public void testObtainVirtualAssistantAuthDetailsFailed() {
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
                new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(false));
        assertThat(virtualAssistantModel.getBotId(), equalTo("noRemoteServer"));
        assertThat(virtualAssistantModel.getClientToken(), equalTo("noRemoteServer"));
        assertThat(virtualAssistantModel.getErrorMessage(), equalTo(String.valueOf(HttpStatus.UNAUTHORIZED.value())));
    }

}