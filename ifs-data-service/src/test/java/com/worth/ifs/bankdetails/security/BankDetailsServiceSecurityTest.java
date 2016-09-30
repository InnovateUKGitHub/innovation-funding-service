package com.worth.ifs.bankdetails.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.bankdetails.transactional.BankDetailsService;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Collections.singletonList;

public class BankDetailsServiceSecurityTest extends BaseServiceSecurityTest<BankDetailsService> {
    @Override
    protected Class<TestBankDetailsService> getClassUnderTest() {
        return TestBankDetailsService.class;
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfCompAdminRole() {
        Long projectId = 123L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
        classUnderTest.getProjectBankDetailsStatusSummary(projectId);
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryAllowedIfProjectFinanceRole() {
        Long projectId = 123L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        classUnderTest.getProjectBankDetailsStatusSummary(projectId);
    }

    @Test
    public void testGetProjectBankDetailsStatusSummaryNotAllowedIfNoInternalRolesAtall() {
        Long projectId = 123L;
        try {
            classUnderTest.getProjectBankDetailsStatusSummary(projectId);
            Assert.fail("Should not have been able to get project bank details status summary from project without either Comp Admin or project finance role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
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
