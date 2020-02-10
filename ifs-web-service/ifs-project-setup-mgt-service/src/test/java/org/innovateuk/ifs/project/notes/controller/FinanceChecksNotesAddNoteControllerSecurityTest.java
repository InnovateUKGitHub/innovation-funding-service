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

public class FinanceChecksNotesAddNoteControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksNotesAddNoteController> {

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
    protected Class<? extends FinanceChecksNotesAddNoteController> getClassUnderTest() {
        return FinanceChecksNotesAddNoteController.class;
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksNotesSection(eq(projectCompositeId), isA(UserResource.class));
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, null, null, null);
                Assert.fail("Should not have been able to cancel form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDownloadAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L, null, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveQuery() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveNote(projectCompositeId.id(), 2L, null, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.saveNote(projectCompositeId.id(), 2L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to save note without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveQueryAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveNewNoteAttachment(null, projectCompositeId.id(), 2L, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.saveNewNoteAttachment(null, projectCompositeId.id(), 2L, null, null, null, null, null, null);
                Assert.fail("Should not have been able to save a note attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewQuery() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.viewNewNote(projectCompositeId.id(), 2L, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.viewNewNote(projectCompositeId.id(), 2L, null, null, null, null);
                Assert.fail("Should not have been able to show the create note form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, null, null, null, null, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create note form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
