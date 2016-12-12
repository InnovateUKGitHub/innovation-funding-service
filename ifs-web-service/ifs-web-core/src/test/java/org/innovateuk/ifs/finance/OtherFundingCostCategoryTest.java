package org.innovateuk.ifs.finance;

import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OtherFundingCostCategoryTest {
    OtherFundingCostCategory otherFundingCostCategory;

    @Before
    public void setUp() throws Exception {
        otherFundingCostCategory = new OtherFundingCostCategory();
    }

    @Test
    public void excludeFromTotalCostShouldReturnBoolean() throws Exception{
        Assert.assertEquals(otherFundingCostCategory.excludeFromTotalCost(), true);
    }
}
