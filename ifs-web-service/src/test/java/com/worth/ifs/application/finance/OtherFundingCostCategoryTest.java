package com.worth.ifs.application.finance;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

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