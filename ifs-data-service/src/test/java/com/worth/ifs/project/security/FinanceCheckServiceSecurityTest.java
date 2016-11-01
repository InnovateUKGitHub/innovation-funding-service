package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.transactional.FinanceCheckService;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.fail;

public class FinanceCheckServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckService> {

    @Test
    public void testGetFinanceCheckByProjectAndOrganisation() {
        assertRolesCanPerform(() -> classUnderTest.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(1L, 2L)), PROJECT_FINANCE);
    }

    @Test
    public void testSaveFinanceCheck() {
        assertRolesCanPerform(() -> classUnderTest.save(null), PROJECT_FINANCE);
    }

    @Test
    public void testApproveFinanceCheck() {
        assertRolesCanPerform(() -> classUnderTest.approve(1L, 2L), PROJECT_FINANCE);
    }

    @Test
    public void testGetFinanceCheckApprovalStatus(){
        assertRolesCanPerform(() -> classUnderTest.getFinanceCheckApprovalStatus(1L, 2L), PROJECT_FINANCE);
    }

    @Test
    public void testGetFinanceCheckSummary(){
        assertRolesCanPerform(() -> classUnderTest.getFinanceCheckSummary(1L), PROJECT_FINANCE);
    }

    private void assertInternalRolesCanPerform(Runnable actionFn) {
        assertRolesCanPerform(actionFn, COMP_ADMIN, PROJECT_FINANCE);
    }

    private void assertRolesCanPerform(Runnable actionFn, UserRoleType... supportedRoles) {
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (asList(supportedRoles).contains(role)) {
                actionFn.run();
            } else {
                try {
                    actionFn.run();
                    fail("Should have thrown an AccessDeniedException for any non " + supportedRoles + " users");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Override
    protected Class<TestFinanceCheckService> getClassUnderTest() {
        return TestFinanceCheckService.class;
    }

    public static class TestFinanceCheckService implements FinanceCheckService {

        @Override
        public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
            return null;
        }

        @Override
        public ServiceResult<Void> save(FinanceCheckResource toUpdate) {
            return null;
        }

        @Override
        public ServiceResult<Void> approve(Long projectId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(Long projectId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
            return null;
        }
    }
}

