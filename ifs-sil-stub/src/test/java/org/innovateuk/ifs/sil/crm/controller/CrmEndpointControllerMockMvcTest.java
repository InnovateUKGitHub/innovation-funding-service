package org.innovateuk.ifs.sil.crm.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.crm.controller.CrmEndpointController;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CrmEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<CrmEndpointController> {

    @Override
    protected CrmEndpointController supplyControllerUnderTest() { return new CrmEndpointController(); }

    @Test
    public void testUpdateContact() throws Exception {
        SilContact contact = new SilContact();
        contact.setIfsUuid("1");
        contact.setIfsAppID("1");
        contact.setFirstName("First");
        contact.setLastName("Last");
        String requestBody = objectMapper.writeValueAsString(contact);

        mockMvc.
                perform(
                        post("/silstub/contacts").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isAccepted()).
                andReturn();
    }

    @Test
    public void testMarkApplicationIneligible() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        application.setApplicationID(1);
        application.setMarkedIneligible(true);
        application.setEligibilityStatusChangeDate(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));  //("yyyy-MM-dd'T'HH.nn.ss'Z'")
        application.setEligibilityStatusChangeSource("IFS");

        String requestBody = objectMapper.writeValueAsString(application);
        // TODO-10472 update endpoint once SF ready
        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isOk()).
                andReturn();
    }


    @Test
    public void testIncompleteMarkApplicationIneligible() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        String requestBody = objectMapper.writeValueAsString(application);
        // TODO-10472 update endpoint once SF ready
        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isBadRequest()).
                andReturn();
    }

    @Test
    public void testReinstateApplication() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        application.setApplicationID(1);
        application.setMarkedIneligible(false);
        application.setEligibilityStatusChangeDate(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));  //("yyyy-MM-dd'T'HH.nn.ss'Z'")
        application.setEligibilityStatusChangeSource("IFS");

        String requestBody = objectMapper.writeValueAsString(application);
        // TODO-10472 update endpoint once SF ready
        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isOk()).
                andReturn();
    }


    @Test
    public void testIncompleteReinstateApplication() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        String requestBody = objectMapper.writeValueAsString(application);
        // TODO-10472 update endpoint once SF ready
        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isBadRequest()).
                andReturn();
    }

}