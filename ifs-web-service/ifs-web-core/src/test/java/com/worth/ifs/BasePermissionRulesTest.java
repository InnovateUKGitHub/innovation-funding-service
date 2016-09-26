package com.worth.ifs;

import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<RoleResource> allRolesResources;

    protected List<UserResource> allGlobalRoleUsers;

    protected RoleResource compAdminRole() {
        return getRoleResource(COMP_ADMIN);
    }

    protected RoleResource assessorRole() { return getRoleResource(ASSESSOR); }

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
    }

    protected UserResource projectFinanceUser() {
        return getUserWithRole(PROJECT_FINANCE);
    }

    protected UserResource assessorUser() {
        return getUserWithRole(ASSESSOR);
    }

    protected RoleResource systemRegistrationRole() {
        return getRoleResource(SYSTEM_REGISTRATION_USER);
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    @Before
    public void setupSetsOfData() {
        allRolesResources = stream(UserRoleType.values()).map(role -> newRoleResource().withType(UserRoleType.fromName(role.getName())).build()).collect(toList());
        allGlobalRoleUsers = simpleMap(allRolesResources, role -> newUserResource().withRolesGlobal(singletonList(role)).build());
    }

    private UserResource createUserWithRoles(UserRoleType... types) {
        List<RoleResource> roles = simpleMap(asList(types), this::getRoleResource);
        return newUserResource().withRolesGlobal(roles).build();
    }

    protected UserResource getUserWithRole(UserRoleType type) {
        return simpleFilter(allGlobalRoleUsers, user -> simpleMap(user.getRoles(), RoleResource::getName).contains(type.getName())).get(0);
    }

    private RoleResource getRoleResource(UserRoleType type) {
        return simpleFilter(allRolesResources, role -> role.getName().equals(type.getName())).get(0);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
