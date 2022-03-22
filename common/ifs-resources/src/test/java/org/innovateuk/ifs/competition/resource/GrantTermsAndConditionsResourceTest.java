package org.innovateuk.ifs.competition.resource;

import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GrantTermsAndConditionsResourceTest {

    @Test
    public void isProcurementThirdParty() {
        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Third Party")
                .build();

        assertTrue(grantTermsAndConditionsResource.isProcurementThirdParty());
    }

    @Test
    public void isNotProcurementThirdParty() {
        GrantTermsAndConditionsResource grantTermsAndConditionsResource = newGrantTermsAndConditionsResource()
                .withName("Procurement")
                .build();

        assertFalse(grantTermsAndConditionsResource.isProcurementThirdParty());
    }
}
