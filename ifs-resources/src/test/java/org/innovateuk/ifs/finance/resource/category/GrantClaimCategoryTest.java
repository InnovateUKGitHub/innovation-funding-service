package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaim;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostCategoryBuilder.newGrantClaimCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrantClaimCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private ExcludedCostCategory grantClaimCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem grantClaim = newGrantClaim().withGrantClaimPercentage(10).build();
        FinanceRowItem grantClaim2 = newGrantClaim().withGrantClaimPercentage(20).build();

        costs.add(grantClaim);
        costs.add(grantClaim2);

        grantClaimCategory = newGrantClaimCostCategory().withCosts(asList(grantClaim, grantClaim2)).build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, grantClaimCategory.getCosts());
    }

    @Test
    public void getTotal() {

        grantClaimCategory.calculateTotal();
        assertEquals(new BigDecimal(30), grantClaimCategory.getTotal());
    }

    @Test
    public void addCost() {

        FinanceRowItem grantClaim3 = new GrantClaimPercentage(3L, 30, 1L);
        costs.add(grantClaim3);
        grantClaimCategory.addCost(grantClaim3);

        assertEquals(costs, grantClaimCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertTrue(grantClaimCategory.excludeFromTotalCost());
    }
}