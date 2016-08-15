package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ProjectFinanceServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceService> {

    private ProjectFinancePermissionRules projectFinancePermissionRules;

    @Before
    public void lookupPermissionRules() {
        projectFinancePermissionRules = getMockPermissionRulesBean(ProjectFinancePermissionRules.class);
    }

    @Test
    public void testGenerateSpendProfile() {

        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);

            if (PROJECT_FINANCE.equals(role)) {
                service.generateSpendProfile(123L);
            } else {
                try {
                    service.generateSpendProfile(123L);
                    fail("Should have thrown an AccessDeniedException for any non-Finance Team members");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Test
    public void testGetSpendProfileTable() {

        Long projectId = 1L;
        Long organisationId = 1L;

        assertAccessDenied(() -> service.getSpendProfileTable(projectId, organisationId),
                () -> {

                    verify(projectFinancePermissionRules).partnersCanViewTheirOwnSpendProfileData("" + projectId + ":" + organisationId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetSpendProfileTableWithProjectFinanceRole() {

        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);

            if (PROJECT_FINANCE.equals(role)) {
                service.getSpendProfileTable(1L, 1L);
            } else {
                try {
                    service.getSpendProfileTable(1L, 1L);
                    fail("Should have thrown an AccessDeniedException for any non-Finance Team members");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Test
    public void testGetSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;

        assertAccessDenied(() -> service.getSpendProfile(projectId, organisationId),
                () -> {

                    verify(projectFinancePermissionRules).partnersCanViewTheirOwnSpendProfileData("" + projectId + ":" + organisationId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetSpendProfileWithProjectFinanceRole() {

        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);

            if (PROJECT_FINANCE.equals(role)) {
                service.getSpendProfile(1L, 1L);
            } else {
                try {
                    service.getSpendProfile(1L, 1L);
                    fail("Should have thrown an AccessDeniedException for any non-Finance Team members");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Override
    protected Class<TestProjectFinanceService> getServiceClass() {
        return TestProjectFinanceService.class;
    }

    public static class TestProjectFinanceService implements ProjectFinanceService {

        @Override
        public ServiceResult<Void> generateSpendProfile(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<SpendProfileTableResource> getSpendProfileTable(Long projectId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<SpendProfileResource> getSpendProfile(Long projectId, Long organisationId) {
            return null;
        }
    }
}

