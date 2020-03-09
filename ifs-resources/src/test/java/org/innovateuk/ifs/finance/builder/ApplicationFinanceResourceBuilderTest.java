package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;

public class ApplicationFinanceResourceBuilderTest {

    @Test
    public void setGrantClaimPercentage() {
        ApplicationFinanceResource finance = newApplicationFinanceResource().withGrantClaimPercentage(BigDecimal.valueOf(25)).build();
        assertEquals(BigDecimal.valueOf(25), finance.getGrantClaimPercentage());
    }
}
