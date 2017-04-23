package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectGrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectSetUpGrantOfferController> {

    @Override
    protected Class<? extends ProjectSetUpGrantOfferController> getClassUnderTest() {
        return ProjectSetUpGrantOfferController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.confirmation(123L, null));
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(123L));
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(123L));
        assertSecured(() -> classUnderTest.submit(123L, null, null, null, null, null));
        assertSecured(() -> classUnderTest.uploadSignedGrantOfferLetterFile(123L, null, null, null, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class));
    }
}
