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
    private BigDecimal total = BigDecimal.ZERO;

    private FinanceRowItem grantClaim;
    private FinanceRowItem grantClaim2;

    private GrantClaimCategory grantClaimCategory;

    @Before
    public void setUp() throws Exception {
        grantClaim = new GrantClaim(1L, 50);
        grantClaim2 = new GrantClaim(2L, 30);
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

        assertEquals(total, grantClaimCategory.getTotal());
    }

    @Test
    public void calculateTotal() {

        grantClaimCategory.calculateTotal();

        assertEquals(new BigDecimal(80), grantClaimCategory.getTotal());
    }

    @Test
    public void addCost() {

        assertEquals(grantClaim, grantClaimCategory.getCosts().get(0));
    }

    @Test
    public void excludeFromTotalCost() {
        assertTrue(grantClaimCategory.excludeFromTotalCost());
    }

}