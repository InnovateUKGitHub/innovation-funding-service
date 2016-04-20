package com.worth.ifs;

import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.domain.UserRoleType.COMP_ADMIN;
import static com.worth.ifs.user.domain.UserRoleType.SYSTEM_REGISTRATION_USER;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<Role> allRoles =  newRole().withType(UserRoleType.values()).build(UserRoleType.values().length);

    protected List<RoleResource> allRolesResources = allRoles.stream().map(role -> newRoleResource().withType(UserRoleType.fromName(role.getName())).build()).collect(toList());

    protected List<UserResource> allRoleUsers = simpleMap(allRolesResources, role -> newUserResource().withRolesGlobal(singletonList(role)).build());

    protected RoleResource compAdminRole() {
        return getRoleResource(COMP_ADMIN);
    }

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
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

    private UserResource createUserWithRoles(UserRoleType... types) {
        List<RoleResource> roles = simpleMap(asList(types), this::getRoleResource);
        return newUserResource().withRolesGlobal(roles).build();
    }

    private UserResource getUserWithRole(UserRoleType type) {
        return simpleFilter(allRoleUsers, user -> simpleMap(user.getRoles(), RoleResource::getName).contains(type.getName())).get(0);
    }

    private RoleResource getRoleResource(UserRoleType type) {
        return simpleFilter(allRolesResources, role -> role.getName().equals(type.getName())).get(0);
    }

    protected Role getRole(UserRoleType type) {
        return simpleFilter(allRoles, role -> role.getName().equals(type.getName())).get(0);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
