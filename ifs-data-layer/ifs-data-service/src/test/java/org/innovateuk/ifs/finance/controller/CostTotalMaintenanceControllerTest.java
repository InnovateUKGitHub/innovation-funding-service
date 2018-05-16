package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSender;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CostTotalMaintenanceControllerTest extends BaseControllerMockMVCTest<CostTotalMaintenanceController> {

    @Mock
    private AllFinanceTotalsSender allFinanceTotalsSenderMock;

    @Override
    protected CostTotalMaintenanceController supplyControllerUnderTest() {
        return new CostTotalMaintenanceController();
    }

    @Test
    public void sendAll() throws Exception {
        when(allFinanceTotalsSenderMock.sendAllFinanceTotals()).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(put("/cost/sendAll"))
                .andExpect(status().isOk());

        verify(allFinanceTotalsSenderMock, only()).sendAllFinanceTotals();
    }
}
