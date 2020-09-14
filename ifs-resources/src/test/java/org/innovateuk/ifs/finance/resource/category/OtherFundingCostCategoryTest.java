package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostBuilder.newOtherFunding;
import static org.innovateuk.ifs.finance.builder.OtherFundingCostCategoryBuilder.newOtherFundingCostCategory;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OtherFundingCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();

    private OtherFunding otherFunding;
    private OtherFunding otherPublicFunding;

    private OtherFundingCostCategory otherFundingCostCategory;

    @Before
    public void setUp() throws Exception {

        otherFunding = newOtherFunding()
                .withOtherPublicFunding("Yes")
                .withFundingSource(OTHER_FUNDING)
                .build();

        otherPublicFunding = newOtherFunding()
                .withFundingAmount(BigDecimal.valueOf(30000))
                .build();

        costs.add(otherPublicFunding);
        otherFundingCostCategory = newOtherFundingCostCategory()
                .withCosts(asList(otherFunding, otherPublicFunding))
                .build();
    }

    @Test
    public void getCosts() {

        assertEquals(costs, otherFundingCostCategory.getCosts());
    }

    @Test
    public void getTotalWithYesOtherPublicFunding() {

        otherFundingCostCategory.calculateTotal();
        assertEquals(otherPublicFunding.getFundingAmount(), otherFundingCostCategory.getTotal());
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
                .withFundingAmount(new BigDecimal(5000))
                .build();
        costs.add(otherFunding2);
        otherFundingCostCategory.addCost(otherFunding2);

        assertEquals(costs, otherFundingCostCategory.getCosts());
    }

    @Test
    public void checkCostCategorySetToExcludeFromTotalCosts() {

        assertTrue(otherFundingCostCategory.excludeFromTotalCost());
    }
}