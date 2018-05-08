package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.TermsAndConditionsController;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.TermsAndConditionsResourceDocs.termsAndConditionsResourceBuilder;
import static org.innovateuk.ifs.documentation.TermsAndConditionsResourceDocs.termsAndConditionsResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsAndConditionsControllerDocumentation extends BaseControllerMockMVCTest<TermsAndConditionsController> {

    @Mock
    TermsAndConditionsService termsAndConditionsService;

    @Override
    protected TermsAndConditionsController supplyControllerUnderTest() {
        return new TermsAndConditionsController();
    }

    @Test
    public void getById() throws Exception {
        Long termsAndConditionsId = 1L;
        when(termsAndConditionsService.getById(termsAndConditionsId)).thenReturn(serviceSuccess(termsAndConditionsResourceBuilder.build()));

        mockMvc.perform(get("/terms-and-conditions/getById/{id}", termsAndConditionsId))
                .andExpect(status().isOk())
                .andDo(document("terms-and-conditions/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("The terms and conditions which need to be collected")
                        ),
                        responseFields(termsAndConditionsResourceFields)));
    }

    @Test
    public void getLatest() throws Exception {
        List<TermsAndConditionsResource> response = new ArrayList<>();
        response.add(termsAndConditionsResourceBuilder.build());
        when(termsAndConditionsService.getLatestVersionsForAllTermsAndConditions()).thenReturn(serviceSuccess(response));

        mockMvc.perform(get("/terms-and-conditions/getLatest"))
                .andExpect(status().isOk())
                .andDo(document(
                        "terms-and-conditions/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("List of latest versions for all terms and conditions the authenticated user has access to")
                        )
                ));
    }
}
