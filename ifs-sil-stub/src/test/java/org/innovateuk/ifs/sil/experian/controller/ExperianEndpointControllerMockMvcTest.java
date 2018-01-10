package org.innovateuk.ifs.sil.experian.controller;

import org.innovateuk.ifs.sil.AbstractEndpointControllerMockMvcTest;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.sil.experian.controller.ExperianEndpointController.validationErrors;
import static org.innovateuk.ifs.sil.experian.controller.ExperianEndpointController.verificationResults;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests around the SIL email stub
 */
public class ExperianEndpointControllerMockMvcTest extends AbstractEndpointControllerMockMvcTest<ExperianEndpointController> {

    protected ExperianEndpointController supplyControllerUnderTest() {
        return new ExperianEndpointController();
    }

    @Test
    public void testExperianValidate() throws Exception {
        validationErrors.keySet().forEach(bankDetails -> {
            String requestBody = toJson(bankDetails);
            try {
                MvcResult result = mockMvc.
                        perform(
                                post("/silstub/experianValidate").
                                        header("Content-Type", "application/json").
                                        header("IFS_AUTH_TOKEN", "123abc").
                                        content(requestBody)
                        ).
                        andExpect(status().isOk()).
                        andReturn();
            } catch (Exception e) {
                fail("Error while validating a stub example " + e.getMessage() + " " + validationErrors.get(bankDetails));
            }
        });
    }

    @Test
    public void testExperianVerify() throws Exception {
        verificationResults.keySet().forEach(accountDetails -> {
            String requestBody = toJson(accountDetails);
            try {
                MvcResult result = mockMvc.perform(
                        post("/silstub/experianVerify").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(requestBody)
                ).andExpect(status().isOk()).andReturn();
            } catch (Exception e) {
                fail("Error while verification of a stub example " + e.getMessage() + " " + verificationResults.get(accountDetails));
            }
        });
    }
}
