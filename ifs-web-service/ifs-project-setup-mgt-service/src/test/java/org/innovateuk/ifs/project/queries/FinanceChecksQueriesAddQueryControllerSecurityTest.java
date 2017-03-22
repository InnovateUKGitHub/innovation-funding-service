package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesAddQueryController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

public class FinanceChecksQueriesAddQueryControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksQueriesAddQueryController> {

    @Override
    protected Class<? extends FinanceChecksQueriesAddQueryController> getClassUnderTest() {
        return FinanceChecksQueriesAddQueryController.class;
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksQueriesSection(eq(1L), isA(UserResource.class));
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.cancelNewForm(1L, 2L, "", null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.cancelNewForm(1L, 2L, "", null, null, null);
                Assert.fail("Should not have been able to cancel form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDownloadAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.downloadAttachment(1L, 2L, 3L, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.downloadAttachment(1L, 2L, 3L, null, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveQuery() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.saveQuery(1L, 2L, "", null, null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.saveQuery(1L, 2L, "", null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to save query without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveQueryAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.saveNewQueryAttachment(null, 1L, 2L, "", null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.saveNewQueryAttachment(null, 1L, 2L, "", null, null, null, null, null, null);
                Assert.fail("Should not have been able to save a query attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewQuery() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.viewNewQuery(1L, 2L, "", null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.viewNewQuery(1L, 2L, "", null, null, null, null);
                Assert.fail("Should not have been able to show the create query form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.removeAttachment(1L, 2L, "", 3L, null, null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.removeAttachment(1L, 2L, "", 3L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create query form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
