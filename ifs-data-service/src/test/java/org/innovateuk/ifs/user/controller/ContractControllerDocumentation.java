package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ContractDocs.contractResourceBuilder;
import static org.innovateuk.ifs.documentation.ContractDocs.contractResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContractControllerDocumentation extends BaseControllerMockMVCTest<ContractController> {

    private RestDocumentationResultHandler document;

    @Override
    protected ContractController supplyControllerUnderTest() {
        return new ContractController();
    }

    @Before
    public void setUp() throws Exception {
        this.document = document("contract/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findCurrent() throws Exception {
        when(contractServiceMock.getCurrent()).thenReturn(serviceSuccess(contractResourceBuilder.build()));

        mockMvc.perform(get("/contract/findCurrent"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        responseFields(contractResourceFields)
                ));
    }
}
