package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OtherFundingTest {
    private Long id;
    private String otherPublicFunding;
    private String fundingSource;
    private String securedDate;
    private BigDecimal fundingAmount;
    private OtherFunding otherFunding;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        otherPublicFunding = "Liverpool University";
        fundingSource = "University";
        securedDate = "12-2016";
        fundingAmount = new BigDecimal(100243);
        otherFunding = new OtherFunding(id, otherPublicFunding, fundingSource, securedDate, fundingAmount);
    }

    @Test
    public void otherFundingShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(otherFunding.getId().equals(id));
        assert(otherFunding.getOtherPublicFunding().equals(otherPublicFunding));
        assert(otherFunding.getFundingSource().equals(fundingSource));
        assert(otherFunding.getSecuredDate().equals(securedDate));
        assert(otherFunding.getFundingAmount().equals(fundingAmount));
    }

    @Test
    public void calculateTotalsForOtherFundingTest() throws Exception {
        BigDecimal expected = new BigDecimal(100243);
        assertEquals(expected, otherFunding.getTotal());
    }
}
