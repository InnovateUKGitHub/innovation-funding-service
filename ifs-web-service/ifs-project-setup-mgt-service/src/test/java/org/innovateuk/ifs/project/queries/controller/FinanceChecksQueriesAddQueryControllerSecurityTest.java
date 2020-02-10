package org.innovateuk.ifs.project.queries.controller;

import java.util.List;
import java.util.function.Consumer;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.ArgumentMatchers.isA;

public class FinanceChecksQueriesAddQueryControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksQueriesAddQueryController> {

    @Override
    protected Class<? extends FinanceChecksQueriesAddQueryController> getClassUnderTest() {
        return FinanceChecksQueriesAddQueryController.class;
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksAddQuery(isA(ProjectOrganisationCompositeId.class), isA(UserResource.class));
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.cancelNewForm(1L, 2L, "", null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadAttachment(1L, 2L, 3L, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveQuery(1L, 2L, "", null, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.uploadAttachment(null, 1L, 2L, "", null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.uploadAttachment(null, 1L, 2L, "", null, null, null, null, null, null);
                Assert.fail("Should not have been able to save a query attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewQuery() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.viewNew(1L, 2L, "", null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.viewNew(1L, 2L, "", null, null, null, null);
                Assert.fail("Should not have been able to show the create query form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.FINANCE_CONTACT)).build());
        assertSecured(() -> classUnderTest.removeAttachment(1L, 2L, "", 3L, null, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.removeAttachment(1L, 2L, "", 3L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create query form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
