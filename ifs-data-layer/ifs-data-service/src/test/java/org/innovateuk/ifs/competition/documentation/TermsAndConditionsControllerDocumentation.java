package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.TermsAndConditionsController;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.TermsAndConditionsResourceDocs.grantTermsAndConditionsResourceBuilder;
import static org.innovateuk.ifs.documentation.TermsAndConditionsResourceDocs.siteTermsAndConditionsResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsAndConditionsControllerDocumentation extends BaseControllerMockMVCTest<TermsAndConditionsController> {

    @Mock
    private TermsAndConditionsService termsAndConditionsService;

    @Override
    protected TermsAndConditionsController supplyControllerUnderTest() {
        return new TermsAndConditionsController();
    }

    @Test
    public void getById() throws Exception {
        Long termsAndConditionsId = 1L;
        when(termsAndConditionsService.getById(termsAndConditionsId)).thenReturn(serviceSuccess
                (grantTermsAndConditionsResourceBuilder.build()));

        mockMvc.perform(get("/terms-and-conditions/get-by-id/{id}", termsAndConditionsId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getLatest() throws Exception {
        List<GrantTermsAndConditionsResource> response = grantTermsAndConditionsResourceBuilder.build(1);
        when(termsAndConditionsService.getLatestVersionsForAllTermsAndConditions()).thenReturn(serviceSuccess
                (response));

        mockMvc.perform(get("/terms-and-conditions/get-latest")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getLatestSiteTermsAndConditions() throws Exception {
        SiteTermsAndConditionsResource siteTermsAndConditionsResource = siteTermsAndConditionsResourceBuilder.build();

        when(termsAndConditionsService.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess
                (siteTermsAndConditionsResource));

        mockMvc.perform(get("/terms-and-conditions/site")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
