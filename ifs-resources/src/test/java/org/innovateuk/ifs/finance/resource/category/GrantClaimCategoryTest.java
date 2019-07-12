package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrantClaimCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();

    private FinanceRowItem grantClaim;
    private FinanceRowItem grantClaim2;

    private GrantClaimCategory grantClaimCategory;

    @Before
    public void setUp() throws Exception {

        grantClaim = new GrantClaim(1L, 10);
        grantClaim2 = new GrantClaim(2L, 20);

        costs.add(grantClaim);
        costs.add(grantClaim2);

        grantClaimCategory = new GrantClaimCategory();
        grantClaimCategory.setCosts(costs);
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

        FinanceRowItem grantClaim3 = new GrantClaim(3L, 30);
        grantClaimCategory.addCost(grantClaim3);
        assertEquals(costs, grantClaimCategory.getCosts());
    }

    @Test
    public void excludeFromTotalCost() {
        assertTrue(grantClaimCategory.excludeFromTotalCost());
    }

}