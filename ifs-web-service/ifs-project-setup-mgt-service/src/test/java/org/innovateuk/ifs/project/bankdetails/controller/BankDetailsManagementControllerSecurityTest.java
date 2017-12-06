package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class BankDetailsManagementControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<BankDetailsManagementController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends BankDetailsManagementController> getClassUnderTest() {
        return BankDetailsManagementController.class;
    }

    @Test
    public void testApproveBankDetails() {
        assertSecured(() -> classUnderTest.approveBankDetails(null, null, null, null, projectCompositeId.id(), 234L, null));
    }

    @Test
    public void testChangeBankDetails() {
        assertSecured(() -> classUnderTest.changeBankDetails(null, projectCompositeId.id(), 234L, null, null, null, null));
    }

    @Test
    public void testChangeBankDetailsView() {
        assertSecured(() -> classUnderTest.changeBankDetailsView(null, projectCompositeId.id(), 345L, null, null));
    }

    @Test
    public void testViewBankDetails() {
        assertSecured(() -> classUnderTest.viewBankDetails(null, projectCompositeId.id(), 234L, null));
    }

    @Test
    public void testViewPartnerBankDetails() {
        assertSecured(() -> classUnderTest.viewPartnerBankDetails(null, projectCompositeId.id(), null));
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessBankDetailsSection(eq(ProjectCompositeId.id(123L)), isA(UserResource.class));
    }
}
