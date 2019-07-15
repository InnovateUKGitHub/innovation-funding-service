package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OtherFundingCostCategoryTest {

    private OtherFunding otherFunding;
    private List<FinanceRowItem> costs = new ArrayList<>();

    private OtherFundingCostCategory otherFundingCostCategory;

    @Before
    public void setUp() throws Exception {

        otherFunding = newOtherFunding()
                .withFundingSource("Luck")
                .withName("Lottery")
                .withFundingAmount(new BigDecimal(10000))
                .build();

        costs.add(otherFunding);

        otherFundingCostCategory = new OtherFundingCostCategory();
        otherFundingCostCategory.setCosts(costs);
        otherFundingCostCategory.setOtherFunding(otherFunding);
    }

    @Test
    public void getCosts() {

        assertEquals(costs, otherFundingCostCategory.getCosts());
    }

    @Test
    public void getTotalWithYesOtherPublicFunding() {

        otherFunding.setOtherPublicFunding("Yes");
        otherFundingCostCategory.calculateTotal();

        assertEquals(new BigDecimal(10000), otherFundingCostCategory.getTotal());
    }

    @Test
    public void getTotalWithNoOtherPublicFunding() {

        otherFunding.setOtherPublicFunding("");
        otherFundingCostCategory.calculateTotal();

        assertEquals(BigDecimal.ZERO, otherFundingCostCategory.getTotal());
    }

    @Test
    public void getOtherFunding() {

        assertEquals(otherFunding, otherFundingCostCategory.getOtherFunding());
    }

    @Test
    public void getOtherPublicFunding() {

        otherFunding.setOtherPublicFunding("Yes");
        assertEquals("Yes", otherFundingCostCategory.getOtherPublicFunding());
    }

    @Test
    public void getOtherPublicFundingWithNullOtherFunding() {

        otherFunding.setOtherPublicFunding("");
        assertEquals("", otherFundingCostCategory.getOtherPublicFunding());
    }

    @Test
    public void getOtherFundingCostItem() {

        assertEquals(otherFunding, otherFundingCostCategory.getOtherFundingCostItem());
    }

    @Test
    public void addCost() {

        FinanceRowItem otherFunding2 = newOtherFunding()
                .withFundingSource("Family")
                .withName("Savings")
                .withFundingAmount(new BigDecimal(5000))
                .build();
        costs.add(otherFunding2);
        otherFundingCostCategory.addCost(otherFunding2);

        assertEquals(costs, otherFundingCostCategory.getCosts());
    }

    @Test
    public void excludeFromTotalCost() {

        assertTrue(otherFundingCostCategory.excludeFromTotalCost());
    }
}