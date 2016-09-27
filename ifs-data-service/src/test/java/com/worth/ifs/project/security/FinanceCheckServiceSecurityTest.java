package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.transactional.FinanceCheckService;
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
import static org.mockito.Mockito.verify;

public class FinanceCheckServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckService> {


    @Test
    public void testGetFinanceCheck() {
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (PROJECT_FINANCE.equals(role)) {
                classUnderTest.getById(1L);
            } else {
                try {
                    classUnderTest.getById(1L);
                    fail("Should have thrown an AccessDeniedException for any non project finance users");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }


    @Test
    public void testSaveFinanceCheck() {
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (PROJECT_FINANCE.equals(role)) {
                classUnderTest.save(null);
            } else {
                try {
                    classUnderTest.save(null);
                    fail("Should have thrown an AccessDeniedException for any non project finance users");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Test
    public void testGenerateFinanceCheck() {
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (PROJECT_FINANCE.equals(role) || COMP_ADMIN.equals(role)) {
                classUnderTest.generate(1L);
            } else {
                try {
                    classUnderTest.generate(1L);
                    fail("Should have thrown an AccessDeniedException for any non project finance or comp admin users");
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
        public ServiceResult<FinanceCheckResource> getById(Long id) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckResource> save(FinanceCheckResource toUpdate) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckResource> generate(Long projectId) {
            return null;
        }
    }
}

