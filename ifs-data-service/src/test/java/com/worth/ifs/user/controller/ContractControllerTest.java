package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
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