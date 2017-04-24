package org.innovateuk.ifs.sil.crm.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.config.rest.RestTemplateAdaptorFactory;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;

/**
 *
 */
public class RestSilCrmEndpointTest extends BaseUnitTestMocksTest {

    @Mock
    protected RestTemplate mockRestTemplate;

    @Mock
    protected AsyncRestTemplate mockAsyncRestTemplate;

    private RestSilCrmEndpoint service;

    private AbstractRestTemplateAdaptor adaptor;

    @Before
    public void setupServiceWithMockTemplate() {
        final RestTemplateAdaptorFactory factory = new RestTemplateAdaptorFactory();
        service = new RestSilCrmEndpoint();
        adaptor = factory.silAdaptor();
        ReflectionTestUtils.setField(service, "adaptor", adaptor);
        ReflectionTestUtils.setField(service, "silRestServiceUrl", "http://sil.com");
        ReflectionTestUtils.setField(service, "silCrmContacts", "/silstub/contacts");
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);
        adaptor.setRestTemplate(mockRestTemplate);
    }

    @Test
    public void testUpdateContact() {
        SilContact silContact = new SilContact();
        String expectedUrl = "http://sil.com/silstub/contacts";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>(ACCEPTED);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(silContact), String.class)).thenReturn(returnedEntity);

        ServiceResult<Void> sendMailResult = service.updateContact(silContact);

        assertTrue(sendMailResult.isSuccess());
    }

}
