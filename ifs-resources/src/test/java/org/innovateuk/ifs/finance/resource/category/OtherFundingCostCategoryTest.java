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

public class OtherFundingCostCategoryTest {

    private OtherFunding otherFunding;

    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    private OtherFundingCostCategory otherFundingCostCategory;

    @Before
    public void setUp() throws Exception {

        FinanceRowItem otherFundingFinanceRowItem = newOtherFunding()
                .withFundingSource("Luck")
                .withOtherPublicFunding("Other public funding")
                .withName("Lottery")
                .withFundingAmount(new BigDecimal(10000))
                .build();

        costs.add(otherFundingFinanceRowItem);

        otherFunding = newOtherFunding().withName("Lottery").withFundingAmount(new BigDecimal(20000)).build();

        otherFundingCostCategory = new OtherFundingCostCategory();
        otherFundingCostCategory.setCosts(costs);
    }

    @Test
    public void getCosts() {

        assertEquals(costs, otherFundingCostCategory.getCosts());
    }

    @Test
    public void getTotal() {
    }

    @Test
    public void calculateTotal() {
    }

    @Test
    public void getOtherFunding() {

//        assertEquals(otherFunding, otherFundingCostCategory.getOtherFunding());
    }

    @Test
    public void getOtherPublicFunding() {

//        assertEquals("Other public funding", otherFundingCostCategory.getOtherPublicFunding());
    }

    @Test
    public void getOtherFundingCostItem() {
    }

    @Test
    public void addCost() {
    }

    @Test
    public void excludeFromTotalCost() {
    }

    @Test
    public void setCosts() {
    }

    @Test
    public void otherFundingSet() {
    }
}