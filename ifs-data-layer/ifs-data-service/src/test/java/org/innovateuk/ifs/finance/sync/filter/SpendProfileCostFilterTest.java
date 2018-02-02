package org.innovateuk.ifs.finance.sync.filter;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
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
                .withName(FinanceRowType.LABOUR.getType(),
                        FinanceRowType.OVERHEADS.getType(),
                        FinanceRowType.MATERIALS.getType(),
                        FinanceRowType.CAPITAL_USAGE.getType(),
                        FinanceRowType.SUBCONTRACTING_COSTS.getType(),
                        FinanceRowType.TRAVEL.getType(),
                        FinanceRowType.OTHER_COSTS.getType(),
                        FinanceRowType.FINANCE.getType(),
                        FinanceRowType.YOUR_FINANCE.getType(),
                        FinanceRowType.OTHER_FUNDING.getType(),
                        FinanceRowType.ACADEMIC.getType()).build(11);

        List<FinanceCostTotalResource> financeCostTotalResourceResult = spendProfileCostFilter
                .filterBySpendProfile(financeCostTotalResources);

        List<FinanceCostTotalResource> expectedCostTotalResources = newFinanceCostTotalResource()
                .withName(FinanceRowType.LABOUR.getType(),
                        FinanceRowType.OVERHEADS.getType(),
                        FinanceRowType.MATERIALS.getType(),
                        FinanceRowType.CAPITAL_USAGE.getType(),
                        FinanceRowType.SUBCONTRACTING_COSTS.getType(),
                        FinanceRowType.TRAVEL.getType(),
                        FinanceRowType.OTHER_COSTS.getType()).build(7);

        assertThat(financeCostTotalResourceResult)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedCostTotalResources);
    }
}