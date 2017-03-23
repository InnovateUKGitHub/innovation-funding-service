package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.otherdocuments.controller.ProjectOtherDocumentsController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectOtherDocumentsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectOtherDocumentsController> {

    @Override
    protected Class<? extends ProjectOtherDocumentsController> getClassUnderTest() {
        return ProjectOtherDocumentsController.class;
    }

    @Test
    public void testViewOtherDocumentsPage() {
        assertSecured(() -> classUnderTest.viewOtherDocumentsPage(123L, null, null));
    }

    @Test
    public void testDownloadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.downloadCollaborationAgreementFile(123L));
    }

    @Test
    public void testDownloadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.downloadExploitationPlanFile(123L));
    }

    @Test
    public void testRemoveCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.removeCollaborationAgreementFile(123L, null, null, null, null, null));
    }

    @Test
    public void testRemoveExploitationPlanFile() {
        assertSecured(() -> classUnderTest.removeExploitationPlanFile(123L, null, null, null, null, null));
    }

    @Test
    public void testSubmitPartnerDocuments() {
        assertSecured(() -> classUnderTest.submitPartnerDocuments(null, 123L));
    }

    @Test
    public void testUploadCollaborationAgreementFile() {
        assertSecured(() -> classUnderTest.uploadCollaborationAgreementFile(123L, null, null, null, null, null));
    }

    @Test
    public void testUploadExploitationPlanFile() {
        assertSecured(() -> classUnderTest.uploadExploitationPlanFile(123L, null, null, null, null, null));
    }

    @Test
    public void testViewConfirmDocumentsPage() {
        assertSecured(() -> classUnderTest.viewConfirmDocumentsPage(123L, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.partnerCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class));
    }
}
