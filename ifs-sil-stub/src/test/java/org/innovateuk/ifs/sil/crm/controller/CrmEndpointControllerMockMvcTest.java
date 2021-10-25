package org.innovateuk.ifs.sil.crm.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CrmEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<CrmEndpointController> {

    @Override
    protected CrmEndpointController supplyControllerUnderTest() {
        return new CrmEndpointController();
    }

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
        application.setEligibilityStatusChangeDate(ZonedDateTime.now());  //("yyyy-MM-dd'T'HH.nn.ss'Z'")
        application.setEligibilityStatusChangeSource("IFS");

        String requestBody = objectMapper.writeValueAsString(application);

        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isAccepted()).
                andReturn();
    }


    @Test
    public void testIncompleteMarkApplicationIneligible() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        String requestBody = objectMapper.writeValueAsString(application);

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
        application.setEligibilityStatusChangeDate(ZonedDateTime.now());  //("yyyy-MM-dd'T'HH.nn.ss'Z'")
        application.setEligibilityStatusChangeSource("IFS");

        String requestBody = objectMapper.writeValueAsString(application);

        mockMvc.
                perform(
                        post("/silstub/loanssubmission").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).
                andExpect(status().isAccepted()).
                andReturn();
    }


    @Test
    public void testIncompleteReinstateApplication() throws Exception {
        SilLoanApplication application = new SilLoanApplication();
        String requestBody = objectMapper.writeValueAsString(application);

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