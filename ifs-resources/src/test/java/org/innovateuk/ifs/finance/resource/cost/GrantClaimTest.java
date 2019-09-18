package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

public class GrantClaimTest {
    private Long id;
    private Integer grantClaimPercentage;
    private GrantClaimPercentage grantClaim;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        grantClaimPercentage = 30;
        grantClaim = new GrantClaimPercentage(id, grantClaimPercentage, 1L);
    }

    @Test
    public void grantClaimShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(grantClaim.getId().equals(id));
        assert(grantClaim.getPercentage().equals(grantClaimPercentage));
    }
}
