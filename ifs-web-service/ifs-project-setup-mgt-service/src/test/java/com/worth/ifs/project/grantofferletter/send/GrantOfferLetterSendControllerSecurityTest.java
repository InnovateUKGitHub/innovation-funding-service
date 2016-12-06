package com.worth.ifs.project.grantofferletter.send;

import com.worth.ifs.project.BaseProjectSetupControllerSecurityTest;
import com.worth.ifs.project.ProjectSetupSectionsPermissionRules;
import com.worth.ifs.project.grantofferletter.send.controller.ProjectGrantOfferLetterSendController;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class GrantOfferLetterSendControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectGrantOfferLetterSendController> {

    @Override
    protected Class<? extends ProjectGrantOfferLetterSendController> getClassUnderTest() {
        return ProjectGrantOfferLetterSendController.class;
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
    public void testGrantOfferLetterReceivedByPostPage() {
        assertSecured(() -> classUnderTest.grantOfferLetterReceivedByPost(123L, null, null, null, null));
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
    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessGrantOfferLetterSendSection(eq(123L), isA(UserResource.class));
    }
}
