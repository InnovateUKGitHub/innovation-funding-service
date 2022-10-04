package org.innovateuk.ifs;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * A base class for testing @PermissionRules-annotated classes
 */
public abstract class BasePermissionRulesTest<T> {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();

    protected List<Role> allRolesResources;

    protected List<UserResource> allGlobalRoleUsers;

    protected Role compAdminRole() {
        return Role.COMP_ADMIN;
    }

    protected Role assessorRole() { return Role.ASSESSOR; }

    protected UserResource compAdminUser() {
        return getUserWithRole( Role.COMP_ADMIN);
    }

    protected UserResource projectFinanceUser() {
        return getUserWithRole(PROJECT_FINANCE);
    }

    protected UserResource assessorUser() {
        return getUserWithRole(ASSESSOR);
    }

    protected Role systemRegistrationRole() {
        return SYSTEM_REGISTRATION_USER;
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    @Before
    public void setupSetsOfData() {
        MockitoAnnotations.openMocks(this);
        allRolesResources = asList(Role.values());
        allGlobalRoleUsers = simpleMap(allRolesResources, role -> newUserResource().withRoleGlobal(role).build());
    }

    protected UserResource getUserWithRole(Role type) {
        return simpleFilter(allGlobalRoleUsers, user -> user.getRoles().contains(type)).get(0);
    }

    protected abstract T supplyPermissionRulesUnderTest();

}
