package org.innovateuk.ifs.finance.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FinanceTypeTest {
    Long id;
    String description;
    FinanceType financeType;

    @Before
    public void setUp() throws Exception {
        id = 1L;
        description = "Grant Claim";
        financeType = new FinanceType(id, description);
    }

    @Test
    public void costShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(financeType.getId(), id);
        Assert.assertEquals(financeType.getDescription(), description);
    }
}
