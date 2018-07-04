package org.innovateuk.ifs.finance.documentation;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.finance.controller.CostTotalController;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.transactional.CostTotalService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CostTotalDocs.financeCostTotalResourceBuilder;
import static org.innovateuk.ifs.documentation.CostTotalDocs.financeCostTotalResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostTotalControllerDocumentation extends MockMvcTest<CostTotalController> {

    @Mock
    private CostTotalService costTotalService;

    @Override
    public CostTotalController supplyControllerUnderTest() {
        return new CostTotalController(costTotalService);
    }

    @Test
    public void addCostTotal() throws Exception {
        FinanceCostTotalResource financeCostTotalResource = financeCostTotalResourceBuilder.build();

        when(costTotalService.saveCostTotal(createLambdaMatcher(resource -> {
            assertThat(resource).isEqualToComparingFieldByField(financeCostTotalResource);
        })))
                .thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/cost-total")
                        .content(json(financeCostTotalResource))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andDo(document(
                        "cost-totals/{method-name}",
                        requestFields(financeCostTotalResourceFields)
                ));
    }

    @Test
    public void addCostTotals() throws Exception {
        List<FinanceCostTotalResource> financeCostTotalResources = financeCostTotalResourceBuilder.build(2);

        when(costTotalService.saveCostTotals(createLambdaMatcher(resources -> {
            assertThat(resources)
                    .usingFieldByFieldElementComparator()
                    .containsAll(financeCostTotalResources);
        })))
                .thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/cost-totals")
                        .content(json(financeCostTotalResources))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andDo(document(
                        "cost-totals/{method-name}",
                        requestFields(
                                fieldWithPath("[]").description("List of cost total resources to add.")
                        ).andWithPrefix("[].", financeCostTotalResourceFields)
                ));
    }
}
