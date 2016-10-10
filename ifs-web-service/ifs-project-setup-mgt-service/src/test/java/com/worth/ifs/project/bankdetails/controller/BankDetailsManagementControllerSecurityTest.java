package com.worth.ifs.project.bankdetails.controller;

import com.worth.ifs.bankdetails.controller.BankDetailsManagementController;
import com.worth.ifs.project.BaseProjectSetupControllerSecurityTest;
import com.worth.ifs.project.ProjectSetupSectionsPermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class BankDetailsManagementControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<BankDetailsManagementController> {

    @Override
    protected Class<? extends BankDetailsManagementController> getClassUnderTest() {
        return BankDetailsManagementController.class;
    }

    @Test
    public void testApproveBankDetails() {
        assertSecured(() -> classUnderTest.approveBankDetails(null, 123L, 234L, null));
    }

    @Test
    public void testChangeBankDetails() {
        assertSecured(() -> classUnderTest.changeBankDetails(null, 123L, 234L, null, null, null, null));
    }

    @Test
    public void testChangeBankDetailsView() {
        assertSecured(() -> classUnderTest.changeBankDetailsView(null, 123L, 345L, null, null));
    }

    @Test
    public void testViewBankDetails() {
        assertSecured(() -> classUnderTest.viewBankDetails(null, 123L, 234L, null));
    }

    @Test
    public void testViewPartnerBankDetails() {
        assertSecured(() -> classUnderTest.viewPartnerBankDetails(null, 123L, null));
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessBankDetailsSection(eq(123L), isA(UserResource.class));
    }
}
