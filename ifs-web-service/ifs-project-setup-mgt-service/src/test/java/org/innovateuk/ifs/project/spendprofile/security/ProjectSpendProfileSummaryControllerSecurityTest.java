package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.financechecks.controller.FinanceCheckController;
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

public class ProjectSpendProfileSummaryControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceCheckController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;
    private UserResource userResource;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends FinanceCheckController> getClassUnderTest() {
        return FinanceCheckController.class;
    }

    @Test
    public void testGenerateSpendProfile() {
        assertSecured(() -> classUnderTest.generateSpendProfile(projectCompositeId.id(), null, null, null, null, userResource));
    }

    @Test
    public void testViewSpendProfileSummary() {
        assertSecured(() -> classUnderTest.viewFinanceCheckSummary(projectCompositeId.id(), null, null, userResource));
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksSection(eq(projectCompositeId), isA(UserResource.class));
    }
}
