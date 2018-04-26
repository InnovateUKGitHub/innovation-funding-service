package org.innovateuk.ifs.finance.documentation;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.controller.CostTotalMaintenanceController;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSender;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.Matchers.isEmptyString;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostTotalMaintenanceControllerDocumentation extends MockMvcTest<CostTotalMaintenanceController> {

    @Mock
    private AllFinanceTotalsSender allFinanceTotalsSender;

    @Override
    public CostTotalMaintenanceController supplyControllerUnderTest() {
        return new CostTotalMaintenanceController();
    }

    @Test
    public void sendAll() throws Exception {
        when(allFinanceTotalsSender.sendAllFinanceTotals()).thenReturn(serviceSuccess());

        mockMvc.perform(put("/cost/sendAll"))
                .andExpect(status().isOk())
                .andExpect(content().string(isEmptyString()))
                .andDo(document(
                        "cost-totals/{method-name}"
                ));
    }
}
