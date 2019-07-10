package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrantClaimCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    private FinanceRowItem grantClaim;
    private GrantClaimCategory grantClaimCategory;

    @Before
    public void setUp() throws Exception {
        grantClaim = new GrantClaim(1L, 10);
        costs.add(grantClaim);

        grantClaimCategory = new GrantClaimCategory();
        grantClaimCategory.setCosts(singletonList(grantClaim));
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

        assertEquals(new BigDecimal(10), grantClaimCategory.getTotal());
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