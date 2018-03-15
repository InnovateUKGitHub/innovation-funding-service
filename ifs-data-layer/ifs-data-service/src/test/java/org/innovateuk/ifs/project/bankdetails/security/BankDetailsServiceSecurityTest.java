package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class BankDetailsServiceSecurityTest extends BaseServiceSecurityTest<BankDetailsService> {
    @Override
    protected Class<TestBankDetailsService> getClassUnderTest() {
        return TestBankDetailsService.class;
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfProjectFinanceRole() {

        stream(UserRoleType.values()).forEach(role -> {
            UserResource user = newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build();
            setLoggedInUser(user);

            if (role == PROJECT_FINANCE) {
                classUnderTest.getProjectBankDetailsStatusSummary(123L);
            } else {
                assertAccessDenied(() -> classUnderTest.getProjectBankDetailsStatusSummary(123L), () -> {});
            }
        });

    }

    @Test
    public void getPendingBankDetailsApprovals() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getPendingBankDetailsApprovals(), PROJECT_FINANCE);
    }

    @Test
    public void countPendingBankDetailsApprovals() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.countPendingBankDetailsApprovals(), PROJECT_FINANCE);
    }

    public static class TestBankDetailsService implements BankDetailsService {

        @Override
        public ServiceResult<BankDetailsResource> getById(Long bankDetailsId) {
            return null;
        }

        @Override
        public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> submitBankDetails(@P("bankDetailsResource") BankDetailsResource bankDetailsResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
            return null;
        }

        @Override
        public ServiceResult<ProjectBankDetailsStatusSummary> getProjectBankDetailsStatusSummary(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals() {
            return null;
        }

        @Override
        public ServiceResult<Long> countPendingBankDetailsApprovals() {
            return null;
        }
    }
}
