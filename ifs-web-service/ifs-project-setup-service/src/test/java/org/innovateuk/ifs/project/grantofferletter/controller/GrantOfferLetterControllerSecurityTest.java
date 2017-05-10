package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class GrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<GrantOfferLetterController> {

    @Override
    protected Class<? extends GrantOfferLetterController> getClassUnderTest() {
        return GrantOfferLetterController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.confirmation(123L, null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(123L),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(123L),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.submit(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class)));
        assertSecured(() -> classUnderTest.uploadSignedGrantOfferLetterFile(123L, null, null, null, null, null),
                permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class)));
    }
}
