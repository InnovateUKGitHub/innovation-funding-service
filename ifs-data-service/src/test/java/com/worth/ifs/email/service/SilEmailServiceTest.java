package com.worth.ifs.email.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.names;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.email.builders.EmailAddressResourceBuilder.newEmailAddressResource;
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

        when(endpointMock.sendEmail(expectedMessageToSend)).thenReturn(serviceSuccess(expectedMessageToSend));

        ServiceResult<List<EmailAddress>> emailResult = service.sendEmail(from, to, "A subject", "Some plain text", "Some HTML");
        assertTrue(emailResult.isSuccess());
        assertEquals(to, emailResult.getSuccessObject());

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

        when(endpointMock.sendEmail(expectedMessageToSend)).thenReturn(serviceFailure(internalServerErrorError()));

        ServiceResult<List<EmailAddress>> emailResult = service.sendEmail(from, to, "A subject", "Some plain text", "Some HTML");
        assertTrue(emailResult.isFailure());
        assertTrue(emailResult.getFailure().is(internalServerErrorError()));
    }
}
