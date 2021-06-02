package org.innovateuk.ifs.project.grantofferletter.security;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.grantofferletter.controller.GrantOfferLetterController;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterApprovalForm;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class GrantOfferLetterControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<GrantOfferLetterController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123L);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends GrantOfferLetterController> getClassUnderTest() {
        return GrantOfferLetterController.class;
    }

    @Test
    public void downloadAdditionalContractFile() {
        assertSecured(() -> classUnderTest.downloadAdditionalContractFile(projectCompositeId.id()));
    }

    @Test
    public void downloadGeneratedGrantOfferLetter() {
        assertSecured(() -> classUnderTest.downloadGeneratedGrantOfferLetterFile(projectCompositeId.id()));
    }

    @Test
    public void viewGrantOfferLetterPage() {
        assertSecured(() -> classUnderTest.viewGrantOfferLetterSend(projectCompositeId.id(), null, null));
    }

    @Test
    public void sendGrantOfferLetterPage() {
        assertSecured(() -> classUnderTest.sendGrantOfferLetter(projectCompositeId.id(), null, null, null, null, null));
    }

    @Test
    public void uploadGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.uploadGrantOfferLetterFile(projectCompositeId.id(), null, null, null, null, null));
    }

    @Test
    public void removeGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.removeGrantOfferLetterFile(projectCompositeId.id()));
    }

    @Test
    public void removeAdditionalContractFile() {
        assertSecured(() -> classUnderTest.removeAdditionalContractFile(projectCompositeId.id()));
    }

    @Test
    public void uploadAnnexPage() {
        assertSecured(() -> classUnderTest.uploadAnnexFile(projectCompositeId.id(), null, null, null, null, null));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetter() {
        assertSecured(() -> classUnderTest.signedGrantOfferLetterApproval(projectCompositeId.id(), new GrantOfferLetterApprovalForm(ApprovalType.APPROVED, null)));
    }

    @Test
    public void downloadSignedGrantOfferLetterFile() {
        assertSecured(() -> classUnderTest.downloadSignedGrantOfferLetterFile(projectCompositeId.id()));
    }

    @Test
    public void downloadSignedAdditionalContractFile() {
        assertSecured(() -> classUnderTest.downloadSignedAdditionalContractFile(projectCompositeId.id()));
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessGrantOfferLetterSendSection(eq(projectCompositeId), isA(UserResource.class));
    }
}