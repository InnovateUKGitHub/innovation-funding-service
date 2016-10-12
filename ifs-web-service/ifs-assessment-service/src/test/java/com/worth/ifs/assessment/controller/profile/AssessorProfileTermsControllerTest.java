package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.profile.AssessorProfileTermsModelPopulator;
import com.worth.ifs.user.resource.ContractResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorProfileTermsControllerTest extends BaseControllerMockMVCTest<AssessorProfileTermsController> {

    @Spy
    @InjectMocks
    private AssessorProfileTermsModelPopulator assessorProfileTermsModelPopulator;

    @Override
    protected AssessorProfileTermsController supplyControllerUnderTest() {
        return new AssessorProfileTermsController();
    }

    @Test
    public void getTerms() throws Exception {
        String termsValue = "testTermsValue";
        ContractResource contractResource = newContractResource().withText(termsValue).build();

        when(contractRestService.getCurrentContract()).thenReturn(restSuccess(contractResource));

        mockMvc.perform(get("/profile/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("profile/terms"));
    }

    @Test
    public void submitTerms() throws Exception {

    }
}