package org.innovateuk.ifs.sil.email.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailBody;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static org.innovateuk.ifs.commons.service.HttpHeadersUtils.getJSONHeaders;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 *
 */
public class RestSilEmailEndpointTest {


    @Mock
    protected RestTemplate mockRestTemplate;

    @Mock
    protected AsyncRestTemplate mockAsyncRestTemplate;

    private RestSilEmailEndpoint service;

    private AbstractRestTemplateAdaptor adaptor;

    @Before
    public void setupServiceWithMockTemplate() {
        MockitoAnnotations.initMocks(this);
        BaseBuilderAmendFunctions.clearUniqueIds();
        service = new RestSilEmailEndpoint();
        adaptor = new AbstractRestTemplateAdaptor() {
            @Override
            public HttpHeaders getHeaders() {
                return getJSONHeaders();
            }
        };
        ReflectionTestUtils.setField(service, "adaptor", adaptor);
        ReflectionTestUtils.setField(service, "silRestServiceUrl", "http://sil.com");
        ReflectionTestUtils.setField(service, "silSendmailPath", "/silstub/sendmail");
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);
        adaptor.setRestTemplate(mockRestTemplate);
    }

    @Test
    public void testSendEmail() {

        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");
        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);

        String expectedUrl = "http://sil.com/silstub/sendmail";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>(ACCEPTED);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(silEmail), String.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isSuccess());
        assertEquals(silEmail, sendMailResult.getSuccess());
    }

    @Test
    public void testSendEmailNotAccepted() {

        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");
        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);

        String expectedUrl = "http://sil.com/silstub/sendmail";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>("Failure!", BAD_REQUEST);

        when(mockRestTemplate.postForEntity(expectedUrl, adaptor.jsonEntity(silEmail), String.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isFailure());
        assertTrue(sendMailResult.getFailure().is(EMAILS_NOT_SENT_MULTIPLE));
    }

    @Test
    public void testSendEmailButRestTemplateThrowsException() {

        SilEmailMessage silEmail = new SilEmailMessage(null, null, "A subject");

        when(mockRestTemplate.postForEntity("http://sil.com/silstub/sendmail", adaptor.jsonEntity(silEmail), String.class)).thenThrow(new IllegalArgumentException("no posting!"));

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isFailure());
        assertTrue(sendMailResult.getFailure().is(internalServerErrorError()));
    }

}
