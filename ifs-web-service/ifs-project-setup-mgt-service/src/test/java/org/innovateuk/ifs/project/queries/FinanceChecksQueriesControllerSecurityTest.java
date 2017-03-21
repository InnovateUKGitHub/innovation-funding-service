package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.sections.security.ProjectSetupSectionsPermissionRules;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
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

public class FinanceChecksQueriesControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksQueriesController> {

    @Override
    protected Class<? extends FinanceChecksQueriesController> getClassUnderTest() {
        return FinanceChecksQueriesController.class;
    }

    @Override
    protected Consumer<ProjectSetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksQueriesSection(eq(1L), isA(UserResource.class));
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
    public void testShowPage() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.showPage(1L, 2L, "", null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.showPage(1L, 2L, "", null);
                Assert.fail("Should not have been able to view the page without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.cancelNewForm(1L, 2L, 3L, "", null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.cancelNewForm(1L, 2L, 3L, "", null, null, null, null);
                Assert.fail("Should not have been able to cancel the response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDownloadResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.downloadResponseAttachment(1L, 2L, 3L, 4L, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.downloadResponseAttachment(1L, 2L, 3L, 4L, null, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.saveResponse(null, 1L, 2L, 3L, "", null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.saveResponse(null, 1L, 2L, 3L, "", null, null, null, null, null, null);
                Assert.fail("Should not have been able to save response without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.saveNewResponseAttachment(null, 1L, 2L, 3L, "", null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.saveNewResponseAttachment(null, 1L, 2L, 3L, "", null, null, null, null, null, null);
                Assert.fail("Should not have been able to save a response attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.viewNewResponse(1L, 2L, 3L,"", null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.viewNewResponse(1L, 2L, 3L, "", null, null, null, null);
                Assert.fail("Should not have been able to show the add response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PROJECT_FINANCE).build())).build());
        assertSecured(() -> classUnderTest.removeAttachment(1L, 2L, 3L, "", 4L, null, null, null, null, null, null, null));

        List<UserRoleType> nonFinanceTeamRoles = asList(UserRoleType.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.removeAttachment(1L, 2L, 3L, "", 4L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
