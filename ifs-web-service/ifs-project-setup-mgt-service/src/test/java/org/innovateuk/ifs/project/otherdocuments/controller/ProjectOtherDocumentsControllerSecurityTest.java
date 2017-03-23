package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
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
    public void testGenerateSpendProfile() {
        assertSecured(() -> classUnderTest.acceptOrRejectOtherDocuments(null, null, null, null, 123L, null));
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
    public void testViewOtherDocumentsPage() {
        assertSecured(() -> classUnderTest.viewOtherDocumentsPage(null, null, 123L, null));
    }
    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessOtherDocumentsSection(eq(123L), isA(UserResource.class));
    }
}
