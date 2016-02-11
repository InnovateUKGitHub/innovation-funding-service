package com.worth.ifs.sil.email.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
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
        ResponseEntity<Void> returnedEntity = new ResponseEntity<>(ACCEPTED);
        when(mockRestTemplate.exchange(expectedUrl, POST, httpEntityForRestCall(silEmail), Void.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isSuccess());
        assertEquals(silEmail, sendMailResult.getSuccessObject());
    }

    @Test
    public void testSendEmailNotAccepted() {

        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");
        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);

        ResponseEntity<Void> returnedEntity = new ResponseEntity<>(BAD_REQUEST);
        when(mockRestTemplate.exchange("http://sil.com/silstub/sendmail", POST, httpEntityForRestCall(silEmail), Void.class)).thenReturn(returnedEntity);

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isFailure());
        assertTrue(sendMailResult.getFailure().is(EMAILS_NOT_SENT_MULTIPLE));
    }

    @Test
    public void testSendEmailButRestTemplateThrowsException() {

        SilEmailMessage silEmail = new SilEmailMessage(null, null, "A subject");

        when(mockRestTemplate.exchange("http://sil.com/silstub/sendmail", POST, httpEntityForRestCall(silEmail), Void.class)).thenThrow(new IllegalArgumentException("no posting!"));

        ServiceResult<SilEmailMessage> sendMailResult = service.sendEmail(silEmail);

        assertTrue(sendMailResult.isFailure());
        assertTrue(sendMailResult.getFailure().is(internalServerErrorError()));
    }

}
