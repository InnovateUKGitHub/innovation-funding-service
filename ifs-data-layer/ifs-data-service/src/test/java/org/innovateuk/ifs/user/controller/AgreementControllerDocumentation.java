package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.transactional.AgreementService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AgreementDocs.agreementResourceBuilder;
import static org.innovateuk.ifs.documentation.AgreementDocs.agreementResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AgreementControllerDocumentation extends BaseControllerMockMVCTest<AgreementController> {

    @Mock
    private AgreementService agreementServiceMock;

    @Override
    protected AgreementController supplyControllerUnderTest() {
        return new AgreementController();
    }

    @Test
    public void findCurrent() throws Exception {
        when(agreementServiceMock.getCurrent()).thenReturn(serviceSuccess(agreementResourceBuilder.build()));

        mockMvc.perform(get("/agreement/findCurrent"))
                .andExpect(status().isOk())
                .andDo(document("agreement/{method-name}",
                        responseFields(agreementResourceFields)
                ));
    }
}
