package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class GrantClaimTest {
    private Long id;
    private BigDecimal grantClaimPercentage;
    private GrantClaimPercentage grantClaim;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        grantClaimPercentage = BigDecimal.valueOf(30);
        grantClaim = new GrantClaimPercentage(id, grantClaimPercentage, 1L);
    }

    @Test
    public void grantClaimShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(grantClaim.getId().equals(id));
        assert(grantClaim.getPercentage().equals(grantClaimPercentage));
    }
}
