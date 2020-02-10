package org.innovateuk.ifs.project.notes.controller;

import java.util.List;
import java.util.function.Consumer;
import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class FinanceChecksNotesControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksNotesController> {

    private ProjectLookupStrategy projectLookupStrategy;
    private ProjectCompositeId projectCompositeId;

    @Override
    @Before
    public void lookupPermissionRules() {
        super.lookupPermissionRules();
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        projectCompositeId = ProjectCompositeId.id(123l);
        when(projectLookupStrategy.getProjectCompositeId(projectCompositeId.id())).thenReturn(projectCompositeId);
    }

    @Override
    protected Class<? extends FinanceChecksNotesController> getClassUnderTest() {
        return FinanceChecksNotesController.class;
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksNotesSection(eq(projectCompositeId), isA(UserResource.class));
    }

    @Test
    public void testDownloadAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
            try {
                classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L, null, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testShowPage() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.showPage(projectCompositeId.id(), 2L, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
            try {
                classUnderTest.showPage(projectCompositeId.id(), 2L, null);
                Assert.fail("Should not have been able to view the page without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, 3L, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
            try {
                classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, 3L, null, null, null, null);
                Assert.fail("Should not have been able to cancel the comment form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDownloadResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadResponseAttachment(projectCompositeId.id(), 2L, 3L, 4L, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
            try {
                classUnderTest.downloadResponseAttachment(projectCompositeId.id(), 2L, 3L, 4L, null, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveComment(null, projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
            try {
                classUnderTest.saveComment(null, projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null);
                Assert.fail("Should not have been able to save comment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveNewCommentAttachment(null, projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.saveNewCommentAttachment(null, projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null);
                Assert.fail("Should not have been able to save a comment attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.viewNewComment(projectCompositeId.id(), 2L, 3L, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.viewNewComment(projectCompositeId.id(), 2L, 3L, null, null, null, null);
                Assert.fail("Should not have been able to show the add comment form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, 4L, null, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, 4L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create comment form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
