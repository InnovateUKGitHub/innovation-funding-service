package org.innovateuk.ifs.project.bankdetails;

import org.innovateuk.ifs.project.bankdetails.controller.BankDetailsController;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class BankDetailsControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<BankDetailsController> {


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
    protected Class<? extends BankDetailsController> getClassUnderTest() {
        return BankDetailsController.class;
    }

    @Test
    public void testBankDetails() {
        assertSecured(() -> classUnderTest.bankDetails(null, projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testConfirmBankDetails() {
        assertSecured(() -> classUnderTest.confirmBankDetails(null, null, null, null, projectCompositeId.id(), null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testManualAddress() {
        assertSecured(() -> classUnderTest.manualAddress(null, null, projectCompositeId.id(), null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testSearchAddress() {
        assertSecured(() -> classUnderTest.searchAddress(null, projectCompositeId.id(), null, null, null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testSelectAddress() {
        assertSecured(() -> classUnderTest.selectAddress(null, projectCompositeId.id(), null, null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }

    @Test
    public void testSubmitBankDetails() {
        assertSecured(() -> classUnderTest.submitBankDetails(null, null, null, null, projectCompositeId.id(), null),
                permissionRules -> permissionRules.partnerCanAccessBankDetailsSection(eq(projectCompositeId), isA(UserResource.class)));
    }
}
