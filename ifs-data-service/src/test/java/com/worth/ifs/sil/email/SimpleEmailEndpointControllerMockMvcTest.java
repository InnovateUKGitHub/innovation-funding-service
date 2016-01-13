package com.worth.ifs.sil.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.sil.email.controller.SimpleEmailEndpointController;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 */
public class SimpleEmailEndpointControllerMockMvcTest extends BaseControllerMockMVCTest<SimpleEmailEndpointController> {

    @Override
    protected SimpleEmailEndpointController supplyControllerUnderTest() {
        return new SimpleEmailEndpointController();
    }

    @Test
    public void testSendMail() throws Exception {

        SilEmailAddress from = new SilEmailAddress("Sender", "sender@example.com");
        List<SilEmailAddress> to = singletonList(new SilEmailAddress("Recipient", "recipient@example.com"));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", "Some plain text");
        SilEmailBody htmlBody = new SilEmailBody("text/html", "Some HTML");

        SilEmailMessage silEmail = new SilEmailMessage(from, to, "A subject", plainTextBody, htmlBody);
        String requestBody = new ObjectMapper().writeValueAsString(silEmail);

        mockMvc.
            perform(
                post("/silstub/sendmail").
                    header("Content-Type", "application/json").
                    header("IFS_AUTH_TOKEN", "123abc").
                    content(requestBody)
            ).
            andExpect(status().isAccepted()).
            andDo(document("silstub/sendmail",
                requestHeaders(
                    headerWithName("Content-Type").description("Needs to be application/json"),
                    headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                )
            ));
    }
}

