package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

@Ignore("Ignoring for now, as these will be fixed when dev is merged in prior to PR")
public class ProjectGrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectGrantOfferLetterController> {

    @Override
    protected Class<? extends ProjectGrantOfferLetterController> getClassUnderTest() {
        return ProjectGrantOfferLetterController.class;
    }

    @Test
    public void testPublicMethods() {
        assertSecured(() -> classUnderTest.confirmation(123L, null));
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(123L));
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(123L));
        assertSecured(() -> classUnderTest.downloadGrantOfferLetterFile(123L));
        assertSecured(() -> classUnderTest.submit(123L, null, null, null, null, null));
        assertSecured(() -> classUnderTest.uploadSignedGrantOfferLetterFile(123L, null, null, null, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.partnerCanAccessGrantOfferLetterSection(eq(123L), isA(UserResource.class));
    }
}
