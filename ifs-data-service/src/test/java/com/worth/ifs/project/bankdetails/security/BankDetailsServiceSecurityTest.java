package com.worth.ifs.project.bankdetails.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.project.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.method.P;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;

public class BankDetailsServiceSecurityTest extends BaseServiceSecurityTest<BankDetailsService> {
    @Override
    protected Class<TestBankDetailsService> getClassUnderTest() {
        return TestBankDetailsService.class;
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfProjectFinanceRole() {

        stream(UserRoleType.values()).forEach(role -> {
            UserResource user = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build();
            setLoggedInUser(user);

            if (role == PROJECT_FINANCE) {
                classUnderTest.getProjectBankDetailsStatusSummary(123L);
            } else {
                assertAccessDenied(() -> classUnderTest.getProjectBankDetailsStatusSummary(123L), () -> {});
            }
        });

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
    }
}
