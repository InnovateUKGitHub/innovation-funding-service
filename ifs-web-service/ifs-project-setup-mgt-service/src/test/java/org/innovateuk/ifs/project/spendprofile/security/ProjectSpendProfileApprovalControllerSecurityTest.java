package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.spendprofile.controller.ProjectSpendProfileApprovalController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectSpendProfileApprovalControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<ProjectSpendProfileApprovalController> {

    @Override
    protected Class<? extends ProjectSpendProfileApprovalController> getClassUnderTest() {
        return ProjectSpendProfileApprovalController.class;
    }

    @Test
    public void viewSpendProfileApproval() {
        assertSecured(() -> classUnderTest.viewSpendProfileApproval(123L, null));
    }

    @Test
    public void saveSpendProfileApproval() {
        assertSecured(() -> classUnderTest.saveSpendProfileApproval(123L, null, null, null, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessSpendProfileSection(eq(123L), isA(UserResource.class));
    }
}
