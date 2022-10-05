package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostTotalControllerTest extends MockMvcTest<CostTotalController> {

    @Mock
    private CostTotalService costTotalService;

    @Override
    public CostTotalController supplyControllerUnderTest() {
        return new CostTotalController(costTotalService);
    }

    @Test
    public void addCostTotal() throws Exception {
        FinanceCostTotalResource financeCostTotalResource = new FinanceCostTotalResource(
                FinanceType.APPLICATION,
                FinanceRowType.LABOUR,
                new BigDecimal("999999999.999999"),
                1L
        );

        when(costTotalService.saveCostTotal(financeCostTotalResource)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/cost-total")
                        .content(json(financeCostTotalResource))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());

        verify(costTotalService).saveCostTotal(financeCostTotalResource);
    }

    @Test
    public void addCostTotals() throws Exception {
        List<FinanceCostTotalResource> financeCostTotalResources = newFinanceCostTotalResource()
                .withFinanceType(FinanceType.APPLICATION)
                .withFinanceRowType(FinanceRowType.LABOUR, FinanceRowType.MATERIALS)
                .withFinanceId(1L, 2L)
                .withTotal(new BigDecimal("999.999999"), new BigDecimal("1999.999999"))
                .build(2);

        when(costTotalService.saveCostTotals(financeCostTotalResources)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/cost-totals")
                        .content(json(financeCostTotalResources))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        verify(costTotalService).saveCostTotals(financeCostTotalResources);
    }

}
