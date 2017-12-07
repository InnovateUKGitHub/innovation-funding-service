package org.innovateuk.ifs.project.bankdetails.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.controller.CompetitionsBankDetailsController;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionsBankDetailsControllerDocumentation extends BaseControllerMockMVCTest<CompetitionsBankDetailsController> {

    @Override
    protected CompetitionsBankDetailsController supplyControllerUnderTest() {
        return new CompetitionsBankDetailsController();
    }

    @Test
    public void getPendingBankDetailsApprovals() throws Exception {

        List<BankDetailsReviewResource> pendingBankDetails = singletonList(new BankDetailsReviewResource(1L, 11L, "Comp1", 12L, "project1", 22L, "Org1"));
        when(bankDetailsServiceMock.getPendingBankDetailsApprovals()).thenReturn(serviceSuccess(pendingBankDetails));

        mockMvc.perform(get("/competitions/pending-bank-details-approvals"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingBankDetails)))
                .andDo(document(
                        "competitions/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("List organisations for which Bank Details Approval is pending")
                        )
                ));

        verify(bankDetailsServiceMock, only()).getPendingBankDetailsApprovals();
    }
}
