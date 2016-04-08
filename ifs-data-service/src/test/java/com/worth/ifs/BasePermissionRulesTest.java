package com.worth.ifs;

import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.domain.UserRoleType.SYSTEM_REGISTRATION_USER;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<RoleResource> allRoles = newRoleResource().withType(UserRoleType.values()).build(UserRoleType.values().length);

    protected List<UserResource> allRoleUsers = simpleMap(allRoles, role -> newUserResource().withRolesGlobal(singletonList(role)).build());

    protected RoleResource compAdminRole() {
        return getRole(COMP_ADMIN);
    }

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
    }

    protected RoleResource systemRegistrationRole() {
        return getRole(SYSTEM_REGISTRATION_USER);
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    private UserResource createUserWithRoles(UserRoleType... types) {
        List<RoleResource> roles = simpleMap(asList(types), this::getRole);
        return newUserResource().withRolesGlobal(roles).build();
    }

    private UserResource getUserWithRole(UserRoleType type) {
        return simpleFilter(allRoleUsers, user -> simpleMap(user.getRoles(), RoleResource::getName).contains(type.getName())).get(0);
    }

    private RoleResource getRole(UserRoleType type) {
        return simpleFilter(allRoles, role -> role.getName().equals(type.getName())).get(0);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
