package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.fail;

public class ProjectFinanceServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceService> {

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
    }
}

