package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionsBankDetailsControllerTest extends BaseControllerMockMVCTest<CompetitionsBankDetailsController> {

    @Mock
    private BankDetailsService bankDetailsServiceMock;

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
                .andExpect(content().json(toJson(pendingBankDetails)));

        verify(bankDetailsServiceMock, only()).getPendingBankDetailsApprovals();

    }

    @Test
    public void countPendingBankDetailsApprovals() throws Exception {

        Long pendingBankDetailsCount = 8L;
        when(bankDetailsServiceMock.countPendingBankDetailsApprovals()).thenReturn(serviceSuccess(pendingBankDetailsCount));

        mockMvc.perform(get("/competitions/count-pending-bank-details-approvals"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingBankDetailsCount)));

        verify(bankDetailsServiceMock, only()).countPendingBankDetailsApprovals();

    }
}
