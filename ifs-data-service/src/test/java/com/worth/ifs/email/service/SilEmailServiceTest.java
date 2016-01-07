package com.worth.ifs.email.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.BuilderAmendFunctions.names;
import static com.worth.ifs.email.builders.EmailAddressResourceBuilder.newEmailAddressResource;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

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

        EmailAddressResource from = newEmailAddressResource().with(name("From User")).withEmail("from@email.com").build();
        List<EmailAddressResource> to = newEmailAddressResource().with(names("To User 1", "To User 2")).withEmail("to1@email.com", "to2@email.com").build(2);

        service.sendEmail(from, to, "A subject", "Some plain text", "Not yet implemented HTML");

        SilEmailAddress silEmailFrom = new SilEmailAddress("From User", "from@email.com");
        SilEmailAddress silEmailTo1 = new SilEmailAddress("To User 1", "to1@email.com");
        SilEmailAddress silEmailTo2 = new SilEmailAddress("To User 2", "to2@email.com");
        SilEmailBody silEmailBody = new SilEmailBody("text/plain", "Some plain text");

        SilEmailMessage expectedMessageToSend = new SilEmailMessage(silEmailFrom, asList(silEmailTo1, silEmailTo2), "A subject", silEmailBody);
        verify(endpointMock).sendEmail(expectedMessageToSend);
    }
}
