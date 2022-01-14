package org.innovateuk.ifs.sil.email.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailBody;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests around the SIL email stub
 */
public class SimpleEmailEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<SimpleEmailEndpointController> {

    @Override
    protected SimpleEmailEndpointController supplyControllerUnderTest() {
        SimpleEmailEndpointController endpointController = new SimpleEmailEndpointController();
        ReflectionTestUtils.setField(endpointController, "sendMailFromSilStub", FALSE);
        return endpointController;
    }

    @Test
    public void testSendMail() throws Exception {
        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");

        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);
        String requestBody = objectMapper.writeValueAsString(silEmail);

        mockMvc.
                perform(
                        post("/silstub/sendmail").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isAccepted());
    }
}

