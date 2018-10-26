package org.innovateuk.ifs.sil.grant.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.config.rest.RestTemplateAdaptorFactory;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.service.RestSilCrmEndpoint;
import org.innovateuk.ifs.sil.grant.resource.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;

/**
 *
 */
public class RestGrantEndpointTest extends BaseUnitTestMocksTest {

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
        ReflectionTestUtils.setField(service, "grantSendProject", "/silstub/sendproject");
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);
        adaptor.setRestTemplate(mockRestTemplate);
    }

    @Test
    public void testSendProject() {
        Project project = new Project();
        String expectedUrl = "http://sil.com/silstub/sendproject";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>(ACCEPTED);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(project), String.class))
                .thenReturn(returnedEntity);

        ServiceResult<Void> sendProjectResult = service.sendProject(project);

        assertTrue(sendProjectResult.isSuccess());
    }

}
