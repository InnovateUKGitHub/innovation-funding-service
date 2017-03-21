package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.financecheck.controller.FinanceCheckController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class ProjectSpendProfileSummaryControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceCheckController> {

    @Override
    protected Class<? extends FinanceCheckController> getClassUnderTest() {
        return FinanceCheckController.class;
    }

    @Test
    public void testGenerateSpendProfile() {
        assertSecured(() -> classUnderTest.generateSpendProfile(123L, null, null, null, null));
    }

    @Test
    public void testViewSpendProfileSummary() {
        assertSecured(() -> classUnderTest.viewFinanceCheckSummary(123L, null, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksSection(eq(123L), isA(UserResource.class));
    }
}
