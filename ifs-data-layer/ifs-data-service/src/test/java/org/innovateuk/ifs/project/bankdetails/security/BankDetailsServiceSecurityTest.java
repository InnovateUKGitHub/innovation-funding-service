package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class BankDetailsServiceSecurityTest extends BaseServiceSecurityTest<BankDetailsService> {

    @Override
    protected Class<? extends BankDetailsService> getClassUnderTest() {
        return BankDetailsServiceImpl.class;
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getProjectBankDetailsStatusSummary(123L),
                PROJECT_FINANCE);
    }

    @Test
    public void getPendingBankDetailsApprovals() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getPendingBankDetailsApprovals(), PROJECT_FINANCE);
    }

    @Test
    public void countPendingBankDetailsApprovals() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.countPendingBankDetailsApprovals(),
                PROJECT_FINANCE);
    }
}
