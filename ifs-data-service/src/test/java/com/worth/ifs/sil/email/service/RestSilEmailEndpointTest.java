package com.worth.ifs.sil.email.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.transactional.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.sil.email.service.RestSilEmailEndpoint.ServiceFailures.UNABLE_TO_SEND_MAIL;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 *
 */
public class RestSilEmailEndpointTest extends BaseRestServiceUnitTest<RestSilEmailEndpoint> {

    @Override
    protected RestSilEmailEndpoint registerRestServiceUnderTest() {
        RestSilEmailEndpoint endpoint = new RestSilEmailEndpoint();
        endpoint.silRestServiceUrl = "http://sil.com";
        endpoint.silSendmailPath = "/silstub/sendmail";
        return endpoint;
    }

    @Test
    public void testSendEmail() {

        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");
        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);

        String expectedUrl = "http://sil.com/silstub/sendmail";
        ResponseEntity<String> returnedEntity = new ResponseEntity<>("Success!", ACCEPTED);

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(silEmail), String.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isRight());
        assertEquals(silEmail, sendMailResult.getRight());
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

        when(mockRestTemplate.postForEntity(expectedUrl, httpEntityForRestCall(silEmail), String.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isLeft());
        assertTrue(sendMailResult.getLeft().is(UNABLE_TO_SEND_MAIL));
    }

    @Test
    public void testSendEmailButRestTemplateThrowsException() {

        SilEmailMessage silEmail = new SilEmailMessage(null, null, "A subject");

        when(mockRestTemplate.postForEntity("http://sil.com/silstub/sendmail", httpEntityForRestCall(silEmail), String.class)).thenThrow(new IllegalArgumentException("no posting!"));

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isLeft());
        assertTrue(sendMailResult.getLeft().is(UNEXPECTED_ERROR));
    }

}
