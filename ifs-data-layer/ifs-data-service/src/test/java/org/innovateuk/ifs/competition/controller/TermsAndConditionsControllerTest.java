package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsAndConditionsControllerTest extends BaseControllerMockMVCTest<TermsAndConditionsController>  {

    @Mock
    TermsAndConditionsService termsAndConditionsService;

    @Override
    protected TermsAndConditionsController supplyControllerUnderTest() {
        return new TermsAndConditionsController();
    }

    @Test
    public void getById() throws Exception {
        final Long competitionId = 1L;

        when(termsAndConditionsService.getById(competitionId)).thenReturn(serviceSuccess(newTermsAndConditionsResource().build()));

        mockMvc.perform(get("/terms-and-conditions/getById/{id}", competitionId))
                .andExpect(status().isOk());

        verify(termsAndConditionsService, only()).getById(competitionId);
    }

    @Test
    public void getLatestTermsAndConditions() throws Exception {
        List<TermsAndConditionsResource> termsAndConditionsResourceList = new ArrayList<>();
        termsAndConditionsResourceList.add(newTermsAndConditionsResource().build());

        when(termsAndConditionsService.getLatestTermsAndConditions()).thenReturn(serviceSuccess(termsAndConditionsResourceList));

        mockMvc.perform(get("/terms-and-conditions/getLatest"))
                .andExpect(status().isOk());

        verify(termsAndConditionsService, only()).getLatestTermsAndConditions();
    }
}
