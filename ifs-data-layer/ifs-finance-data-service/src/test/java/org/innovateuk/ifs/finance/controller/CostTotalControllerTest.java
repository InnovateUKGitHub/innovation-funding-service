package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceType;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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
                BigDecimal.valueOf(1000L),
                1L
        );

        Consumer<FinanceCostTotalResource> matchesExpectedResource = (resource) -> {
            assertThat(resource).isEqualToComparingFieldByField(financeCostTotalResource);
        };

        when(costTotalService.saveCostTotal(createLambdaMatcher(matchesExpectedResource))).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/cost-totals")
                        .content(json(financeCostTotalResource))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());

        verify(costTotalService).saveCostTotal(createLambdaMatcher(matchesExpectedResource));
    }
}
