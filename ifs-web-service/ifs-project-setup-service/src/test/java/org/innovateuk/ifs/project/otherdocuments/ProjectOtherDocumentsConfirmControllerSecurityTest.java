package org.innovateuk.ifs.project.otherdocuments;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.status.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.otherdocuments.controller.ProjectOtherDocumentsController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectOtherDocumentsConfirmControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectOtherDocumentsController> {

    @Override
    protected Class<? extends ProjectOtherDocumentsController> getClassUnderTest() {
        return ProjectOtherDocumentsController.class;
    }

    @Test
    public void testViewConfirmDocumentsPage() {
        assertSecured(() -> classUnderTest.viewConfirmDocumentsPage(123L, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.leadPartnerCanSubmitOtherDocumentsSection(eq(123L), isA(UserResource.class));
    }
}

