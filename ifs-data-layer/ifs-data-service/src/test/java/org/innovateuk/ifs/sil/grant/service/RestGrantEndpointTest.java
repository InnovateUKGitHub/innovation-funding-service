package org.innovateuk.ifs.sil.grant.service;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.config.rest.RestTemplateAdaptorFactory;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_PROCESS_SEND_FAILED;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class RestGrantEndpointTest extends BaseUnitTestMocksTest {

    private static final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

    @Mock
    protected RestTemplate mockRestTemplate;

    @Mock
    protected AsyncRestTemplate mockAsyncRestTemplate;

    private RestGrantEndpoint service;

    private AbstractRestTemplateAdaptor adaptor;

    @Before
    public void setupServiceWithMockTemplate() {
        final RestTemplateAdaptorFactory factory = new RestTemplateAdaptorFactory();
        service = new RestGrantEndpoint();
        adaptor = factory.silAdaptor();
        ReflectionTestUtils.setField(service, "adaptor", adaptor);
        ReflectionTestUtils.setField(service, "silRestServiceUrl", "http://sil.com");
        ReflectionTestUtils.setField(service, "path", "/silstub/accprojects");
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);
        adaptor.setRestTemplate(mockRestTemplate);
    }

    @Test
    public void send() {
        Grant grant = new Grant();
        String expectedUrl = "http://sil.com/silstub/accprojects";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>(
                new ObjectNode(jsonNodeFactory).put("Success", "Accepted").toString(), ACCEPTED);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(singletonList(grant)), String.class))
                .thenReturn(returnedEntity);

        ServiceResult<Void> sendProjectResult = service.send(grant);

        assertTrue(sendProjectResult.isSuccess());
    }

    @Test
    public void send_failure() {
        Grant grant = new Grant();
        String expectedUrl = "http://sil.com/silstub/accprojects";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>(INTERNAL_SERVER_ERROR);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(singletonList(grant)), String.class))
                .thenReturn(returnedEntity);

        ServiceResult<Void> sendProjectResult = service.send(grant);

        assertTrue(sendProjectResult.isFailure());
        Error error = sendProjectResult.getFailure().getErrors().get(0);
        assertThat(error.getStatusCode(), equalTo(INTERNAL_SERVER_ERROR));
        assertThat(error.getErrorKey(), equalTo(GRANT_PROCESS_SEND_FAILED.getErrorKey()));
    }

}
