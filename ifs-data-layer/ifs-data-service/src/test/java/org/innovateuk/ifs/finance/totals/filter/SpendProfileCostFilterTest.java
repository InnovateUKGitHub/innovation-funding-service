package org.innovateuk.ifs.finance.totals.filter;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.finance.builder.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;

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
                        FinanceRowType.EQUIPMENT,
                        FinanceRowType.CAPITAL_USAGE,
                        FinanceRowType.OTHER_GOODS,
                        FinanceRowType.HECP_INDIRECT_COSTS,
                        FinanceRowType.SUBCONTRACTING_COSTS,
                        FinanceRowType.TRAVEL,
                        FinanceRowType.OTHER_COSTS,
                        FinanceRowType.PROCUREMENT_OVERHEADS,
                        FinanceRowType.VAT,
                        FinanceRowType.ASSOCIATE_SALARY_COSTS,
                        FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                        FinanceRowType.CONSUMABLES,
                        FinanceRowType.ASSOCIATE_SUPPORT,
                        FinanceRowType.KNOWLEDGE_BASE,
                        FinanceRowType.ESTATE_COSTS,
                        FinanceRowType.KTP_TRAVEL,
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                        FinanceRowType.INDIRECT_COSTS
                )
                .build(21);

        assertThat(financeCostTotalResourceResult)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(expectedCostTotalResources.toArray(new FinanceCostTotalResource[expectedCostTotalResources.size()]));
    }
}