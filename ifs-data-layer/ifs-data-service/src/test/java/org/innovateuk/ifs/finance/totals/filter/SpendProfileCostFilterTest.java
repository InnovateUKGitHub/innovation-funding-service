package org.innovateuk.ifs.finance.totals.filter;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;

public class SpendProfileCostFilterTest {

    private SpendProfileCostFilter spendProfileCostFilter;

    @Before
    public void setUp() throws Exception {
        spendProfileCostFilter = new SpendProfileCostFilter();
    }

    @Test
    public void filterBySpendProfile() {
        List<FinanceCostTotalResource> financeCostTotalResources = newFinanceCostTotalResource()
                .withFinanceRowType(FinanceRowType.values())
                .build(FinanceRowType.values().length);

        List<FinanceCostTotalResource> financeCostTotalResourceResult = spendProfileCostFilter
                .filterBySpendProfile(financeCostTotalResources);

        List<FinanceCostTotalResource> expectedCostTotalResources = newFinanceCostTotalResource()
                .withFinanceRowType(
                        FinanceRowType.LABOUR,
                        FinanceRowType.OVERHEADS,
                        FinanceRowType.MATERIALS,
                        FinanceRowType.CAPITAL_USAGE,
                        FinanceRowType.SUBCONTRACTING_COSTS,
                        FinanceRowType.TRAVEL,
                        FinanceRowType.OTHER_COSTS
                )
                .build(7);

        assertThat(financeCostTotalResourceResult)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedCostTotalResources);
    }
}