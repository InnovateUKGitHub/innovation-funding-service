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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantModel.NO_REMOTE_SERVER_MSG;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VirtualAssistantAuthRestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient = new VirtualAssistantAuthRestClient();

    protected static final String TEST_URL = "http://test";
    protected static final String TEST_BOT_ID = "testBot";
    protected static final String TEST_BOT_SECRET = "testBotSecret";
    protected static final String FAKE_AUTH_STRING = "thiswouldbeajsonauthstring";
    protected static final String FAKE_AUTH_STRING_JSON = JsonUtil.getSerializedObject(FAKE_AUTH_STRING);

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
                .thenReturn(new ResponseEntity<>(FAKE_AUTH_STRING_JSON, HttpStatus.OK));
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(true));
        assertThat(virtualAssistantModel.getBotId(), equalTo(TEST_BOT_ID));
        assertThat(virtualAssistantModel.getClientToken(), equalTo(FAKE_AUTH_STRING));
    }

    @Test
    public void testObtainVirtualAssistantAuthDetailsFailed() {
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
            new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(false));
        assertThat(virtualAssistantModel.getBotId(), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(virtualAssistantModel.getClientToken(), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(virtualAssistantModel.getErrorMessage(), equalTo(String.valueOf(HttpStatus.UNAUTHORIZED.value())));
    }

    @Test
    public void testObtainVirtualAssistantAuthDetailsFailedClientFailure() {
        when(restTemplate.exchange(TEST_URL, HttpMethod.GET,
                new HttpEntity<>(virtualAssistantAuthRestClient.authHeader()), String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(false));
        assertThat(virtualAssistantModel.getBotId(), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(virtualAssistantModel.getClientToken(), equalTo(NO_REMOTE_SERVER_MSG));
        assertThat(virtualAssistantModel.getErrorMessage(),
                equalTo(HttpStatus.BAD_GATEWAY.value() + " " + HttpStatus.BAD_GATEWAY.name()));
    }

}