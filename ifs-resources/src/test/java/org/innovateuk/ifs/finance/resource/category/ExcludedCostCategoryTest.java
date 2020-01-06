package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder.newGrantClaimPercentage;
import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcludedCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private ExcludedCostCategory grantClaimCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem grantClaim = newGrantClaimPercentage().withGrantClaimPercentage(10).build();

        costs.add(grantClaim);

        grantClaimCategory = newExcludedCostCategory().withCosts(asList(grantClaim)).build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, grantClaimCategory.getCosts());
    }

    @Test
    public void getTotal() {

        grantClaimCategory.calculateTotal();
        assertEquals(BigDecimal.ZERO, grantClaimCategory.getTotal());
    }

    @Test
    public void addCost() {

        FinanceRowItem grantClaim3 = newGrantClaimPercentage().withGrantClaimPercentage(30).build();
        costs.add(grantClaim3);
        grantClaimCategory.addCost(grantClaim3);

        assertEquals(costs, grantClaimCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertTrue(grantClaimCategory.excludeFromTotalCost());
    }
}