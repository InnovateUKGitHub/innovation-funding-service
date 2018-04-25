package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.TermsAndConditionsController;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.TermsAndConditionsDocs.siteTermsAndConditionsResourceBuilder;
import static org.innovateuk.ifs.documentation.TermsAndConditionsDocs.termAndConditionsFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsAndConditionsControllerDocumentation extends BaseControllerMockMVCTest<TermsAndConditionsController> {

    @Mock
    private TermsAndConditionsService termsAndConditionsService;

    @Override
    protected TermsAndConditionsController supplyControllerUnderTest() {
        return new TermsAndConditionsController();
    }

    @Test
    public void getLatestSiteTermsAndConditions() throws Exception {
        SiteTermsAndConditionsResource siteTermsAndConditionsResource = siteTermsAndConditionsResourceBuilder.build();

        when(termsAndConditionsService.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess
                (siteTermsAndConditionsResource));

        mockMvc.perform(get("/terms-and-conditions/site"))
                .andExpect(status().isOk())
                .andDo(document("terms-and-conditions/{method-name}",
                        responseFields(termAndConditionsFields)
                ));
    }
}
