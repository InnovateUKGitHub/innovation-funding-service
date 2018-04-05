package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class BankDetailsServiceSecurityTest extends BaseServiceSecurityTest<BankDetailsService> {

    @Override
    protected Class<? extends BankDetailsService> getClassUnderTest() {
        return BankDetailsServiceImpl.class;
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfProjectFinanceRole() {

        stream(Role.values()).forEach(role -> {
            UserResource user = newUserResource().withRolesGlobal(singletonList(role))
                    .build();
            setLoggedInUser(user);

            if (role == PROJECT_FINANCE) {
                classUnderTest.getProjectBankDetailsStatusSummary(123L);
            } else {
                assertAccessDenied(() -> classUnderTest.getProjectBankDetailsStatusSummary(123L), () -> {
                });
            }
        });

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
