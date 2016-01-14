package com.worth.ifs.email.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import com.worth.ifs.transactional.ServiceResult;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.BuilderAmendFunctions.names;
import static com.worth.ifs.email.builders.EmailAddressResourceBuilder.newEmailAddressResource;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing the SilEmailService's interaction with the SIL email endpoint
 */
public class SilEmailServiceTest extends BaseServiceUnitTest<SilEmailService> {

    @Mock
    private SilEmailEndpoint endpointMock;

    @Override
    protected SilEmailService supplyServiceUnderTest() {
        return new SilEmailService();
    }

    @Test
    public void testSendEmail() {

        EmailAddress from = newEmailAddressResource().with(name("From User")).withEmail("from@email.com").build();
        List<EmailAddress> to = newEmailAddressResource().with(names("To User 1", "To User 2")).withEmail("to1@email.com", "to2@email.com").build(2);

        SilEmailAddress silEmailFrom = new SilEmailAddress("From User", "from@email.com");
        SilEmailAddress silEmailTo1 = new SilEmailAddress("To User 1", "to1@email.com");
        SilEmailAddress silEmailTo2 = new SilEmailAddress("To User 2", "to2@email.com");
        SilEmailBody silEmailBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody silEmailBody2 = new SilEmailBody("text/html", "Some HTML");

        SilEmailMessage expectedMessageToSend = new SilEmailMessage(silEmailFrom, asList(silEmailTo1, silEmailTo2), "A subject", silEmailBody, silEmailBody2);

        when(endpointMock.sendEmail(expectedMessageToSend)).thenReturn(success(expectedMessageToSend));

        ServiceResult<List<EmailAddress>> emailResult = service.sendEmail(from, to, "A subject", "Some plain text", "Some HTML");
        assertTrue(emailResult.isRight());
        assertEquals(to, emailResult.getRight());

        verify(endpointMock).sendEmail(expectedMessageToSend);
    }

    @Test
    public void testSendEmailButEndpointFails() {

        EmailAddress from = newEmailAddressResource().with(name("From User")).withEmail("from@email.com").build();
        List<EmailAddress> to = newEmailAddressResource().with(names("To User 1", "To User 2")).withEmail("to1@email.com", "to2@email.com").build(2);

        SilEmailAddress silEmailFrom = new SilEmailAddress("From User", "from@email.com");
        SilEmailAddress silEmailTo1 = new SilEmailAddress("To User 1", "to1@email.com");
        SilEmailAddress silEmailTo2 = new SilEmailAddress("To User 2", "to2@email.com");
        SilEmailBody silEmailBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody silEmailBody2 = new SilEmailBody("text/html", "Some HTML");

        SilEmailMessage expectedMessageToSend = new SilEmailMessage(silEmailFrom, asList(silEmailTo1, silEmailTo2), "A subject", silEmailBody, silEmailBody2);

        when(endpointMock.sendEmail(expectedMessageToSend)).thenReturn(failure(UNEXPECTED_ERROR));

        ServiceResult<List<EmailAddress>> emailResult = service.sendEmail(from, to, "A subject", "Some plain text", "Some HTML");
        assertTrue(emailResult.isLeft());
        assertTrue(emailResult.getLeft().is(UNEXPECTED_ERROR));
    }

    @Test
    public void testSendEmailButEndpointThrowsException() {

        EmailAddress from = newEmailAddressResource().with(name("From User")).withEmail("from@email.com").build();
        List<EmailAddress> to = newEmailAddressResource().with(names("To User 1", "To User 2")).withEmail("to1@email.com", "to2@email.com").build(2);

        SilEmailAddress silEmailFrom = new SilEmailAddress("From User", "from@email.com");
        SilEmailAddress silEmailTo1 = new SilEmailAddress("To User 1", "to1@email.com");
        SilEmailAddress silEmailTo2 = new SilEmailAddress("To User 2", "to2@email.com");
        SilEmailBody silEmailBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody silEmailBody2 = new SilEmailBody("text/html", "Some HTML");

        SilEmailMessage expectedMessageToSend = new SilEmailMessage(silEmailFrom, asList(silEmailTo1, silEmailTo2), "A subject", silEmailBody, silEmailBody2);

        when(endpointMock.sendEmail(expectedMessageToSend)).thenThrow(new IllegalArgumentException("No sending!"));

        ServiceResult<List<EmailAddress>> emailResult = service.sendEmail(from, to, "A subject", "Some plain text", "Some HTML");
        assertTrue(emailResult.isLeft());
        assertTrue(emailResult.getLeft().is(UNEXPECTED_ERROR));
    }

}
