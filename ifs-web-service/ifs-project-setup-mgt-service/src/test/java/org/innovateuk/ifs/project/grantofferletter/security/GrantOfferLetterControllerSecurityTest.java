package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.grantofferletter.controller.GrantOfferLetterController;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class GrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<GrantOfferLetterController> {

    @Override
    protected Class<? extends GrantOfferLetterController> getClassUnderTest() {
        return GrantOfferLetterController.class;
    }

    @Test
    public void testDownloadAdditionalContractFile() {
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(123L));
    }

    @Test
    public void testDownloadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(123L));
    }

    @Test
    public void testViewGrantOfferLetterPage() {
        assertSecured(() -> classUnderTest.viewGrantOfferLetterSend(123L, null, null));
    }

    @Test
    public void testSendGrantOfferLetterPage() {
        assertSecured(() -> classUnderTest.sendGrantOfferLetter(123L, null, null, null, null));
    }

    @Test
    public void testUploadGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.uploadGrantOfferLetterFile(123L, null, null, null, null));
    }

    @Test
    public void testRemoveGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.removeGrantOfferLetterFile(123L, null, null, null, null));
    }

    @Test
    public void testUploadAnnexPage() {
        assertSecured(() -> classUnderTest.uploadAnnexFile(123L, null, null, null, null, null));
    }

    @Test
    public void testApproceOrRejectSignedGrantOfferLetter() {
        assertSecured(() -> classUnderTest.signedGrantOfferLetterApproval(123L, ApprovalType.APPROVED, null, null, null, null));
    }

    @Test
    public void testDownloadSignedGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.downloadSignedGrantOfferLetterFile(123L));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessGrantOfferLetterSendSection(eq(123L), isA(UserResource.class));
    }
}
