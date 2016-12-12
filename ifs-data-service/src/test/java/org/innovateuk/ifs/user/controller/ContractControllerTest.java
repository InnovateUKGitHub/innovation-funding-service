package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

public class ContractControllerTest extends BaseControllerMockMVCTest<ContractController> {

    @Override
    protected ContractController supplyControllerUnderTest() {
        return new ContractController();
    }

    @Test
    public void findCurrentShouldReturnContractResource() throws Exception {
        String contractText = "contract text";

        when(contractServiceMock.getCurrent()).thenReturn(serviceSuccess(newContractResource().withText(contractText).build()));

        mockMvc.perform(get("/contract/findCurrent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(contractText)));
    }
}
