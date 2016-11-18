package com.worth.ifs.finance.builder;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Test;

import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;

public class ApplicationFinanceResourceBuilderTest {

    @Test
    public void testSetGrantClaimPercentage() {
        ApplicationFinanceResource finance = newApplicationFinanceResource().withGrantClaimPercentage(25).build();
        assertEquals(Integer.valueOf(25), finance.getGrantClaimPercentage());
    }
}
