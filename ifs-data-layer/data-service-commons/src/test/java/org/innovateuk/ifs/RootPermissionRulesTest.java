package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.security.evaluator.DefaultPermissionMethodHandler;
import org.innovateuk.ifs.commons.security.evaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * A root class for testing @PermissionRules-annotated classes
 */
public abstract class RootPermissionRulesTest<T> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected T rules = supplyPermissionRulesUnderTest();


    protected List<Role> allRoles;

    protected List<UserResource> allGlobalRoleUsers;

    protected List<UserResource> allInternalUsers;

    protected UserResource compAdminUser() {
        return getUserWithRole(COMP_ADMIN);
    }

    protected UserResource projectFinanceUser() {
        return getUserWithRole(PROJECT_FINANCE);
    }

    protected UserResource supportUser() {
        return getUserWithRole(SUPPORT);
    }

    protected UserResource innovationLeadUser() {
        return getUserWithRole(INNOVATION_LEAD);
    }

    protected UserResource assessorUser() {
        return getUserWithRole(ASSESSOR);
    }

    protected UserResource systemRegistrationUser() {
        return getUserWithRole(SYSTEM_REGISTRATION_USER);
    }

    protected UserResource anonymousUser() {
        return (UserResource) ReflectionTestUtils.getField(new DefaultPermissionMethodHandler(new PermissionedObjectClassToPermissionsToPermissionsMethods()), "ANONYMOUS_USER");
    }

    protected UserResource ifsAdminUser() {
        return getUserWithRole(IFS_ADMINISTRATOR);
    }

    @Before
    public void setupSetsOfData() {
        allRoles = asList(Role.values());
        allGlobalRoleUsers = simpleMap(allRoles, role -> newUserResource().withRolesGlobal(singletonList(role)).build());
        allInternalUsers = asList(compAdminUser(), projectFinanceUser(), supportUser(), innovationLeadUser());

    }

    protected UserResource getUserWithRole(Role type) {
        return simpleFilter(allGlobalRoleUsers, user -> user.hasRole(type)).get(0);
    }

    protected void setUpUserNotAsProjectManager(UserResource user) {
        List<Role> projectManagerUser = emptyList();
        user.setRoles(projectManagerUser);
    }

    protected void setUpUserAsCompAdmin(ProjectResource project, UserResource user) {
        List<Role> compAdminRoleResource = singletonList(COMP_ADMIN);
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserNotAsCompAdmin(ProjectResource project, UserResource user) {
        List<Role> compAdminRoleResource = emptyList();
        user.setRoles(compAdminRoleResource);
    }

    protected void setUpUserAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<Role> projectFinanceUser = singletonList(Role.PROJECT_FINANCE);
        user.setRoles(projectFinanceUser);
    }

    protected void setUpUserNotAsProjectFinanceUser(ProjectResource project, UserResource user) {
        List<Role> projectFinanceUser = emptyList();
        user.setRoles(projectFinanceUser);
    }


    protected abstract T supplyPermissionRulesUnderTest();

}
