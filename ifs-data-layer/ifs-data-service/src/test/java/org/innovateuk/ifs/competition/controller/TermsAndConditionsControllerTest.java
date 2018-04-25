package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder
        .newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsAndConditionsControllerTest extends BaseControllerMockMVCTest<TermsAndConditionsController> {

    @Mock
    private TermsAndConditionsService termsAndConditionsService;

    @Override
    protected TermsAndConditionsController supplyControllerUnderTest() {
        return new TermsAndConditionsController();
    }

    @Test
    public void getLatestSiteTermsAndConditions() throws Exception {
        SiteTermsAndConditionsResource expected = newSiteTermsAndConditionsResource().build();

        when(termsAndConditionsService.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/terms-and-conditions/site"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(termsAndConditionsService, only()).getLatestSiteTermsAndConditions();
    }

}