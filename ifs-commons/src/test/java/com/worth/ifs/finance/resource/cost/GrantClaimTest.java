package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.GrantClaim;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class GrantClaimTest {
    private Long id;
    private Integer grantClaimPercentage;
    private GrantClaim grantClaim;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        grantClaimPercentage = 30;
        grantClaim = new GrantClaim(id, grantClaimPercentage);
    }

    @Test
    public void grantClaimShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(grantClaim.getId().equals(id));
        assert(grantClaim.getGrantClaimPercentage().equals(grantClaimPercentage));
    }
}
