package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.bankdetails.viewmodel.CompetitionPendingBankDetailsApprovalsViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionsBankDetailsControllerTest extends BaseControllerMockMVCTest<CompetitionsBankDetailsController> {
    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Override
    protected CompetitionsBankDetailsController supplyControllerUnderTest() {
        return new CompetitionsBankDetailsController();
    }

    @Test
    public void viewPendingBankDetailsApprovals() throws Exception {

        List<BankDetailsReviewResource> pendingBankDetails = singletonList(new BankDetailsReviewResource(1L, 11L, "Comp1", 12L, "project1", 22L, "Org1"));
        when(bankDetailsRestService.getPendingBankDetailsApprovals()).thenReturn(restSuccess(pendingBankDetails));

        MvcResult result = mockMvc.perform(get("/competitions/status/pending-bank-details-approvals")).
                andExpect(view().name("project/competition-pending-bank-details")).
                andExpect(status().isOk()).
                andReturn();

        CompetitionPendingBankDetailsApprovalsViewModel model = (CompetitionPendingBankDetailsApprovalsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model, new CompetitionPendingBankDetailsApprovalsViewModel(pendingBankDetails));
    }
}
