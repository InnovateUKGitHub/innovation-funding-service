package org.innovateuk.ifs.project.queries.controller;

import org.innovateuk.ifs.project.BaseProjectSetupControllerSecurityTest;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class FinanceChecksQueriesControllerSecurityTest extends BaseProjectSetupControllerSecurityTest<FinanceChecksQueriesController> {


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
    protected Class<? extends FinanceChecksQueriesController> getClassUnderTest() {
        return FinanceChecksQueriesController.class;
    }

    @Override
    protected Consumer<SetupSectionsPermissionRules> getVerification() {
        return permissionRules -> permissionRules.internalCanAccessFinanceChecksQueriesSection(eq(projectCompositeId), isA(UserResource.class));
    }

    @Test
    public void testDownloadAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type -> type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.downloadAttachment(projectCompositeId.id(), 2L, 3L);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testShowPage() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.showPage(projectCompositeId.id(), 2L, "", null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.showPage(projectCompositeId.id(), 2L, "", null);
                Assert.fail("Should not have been able to view the page without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testCancelNewForm() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, 3L, "", null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.cancelNewForm(projectCompositeId.id(), 2L, 3L, "", null, null);
                Assert.fail("Should not have been able to cancel the response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDownloadResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.downloadResponseAttachment(projectCompositeId.id(), 2L, 3L, 4L, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.downloadResponseAttachment(projectCompositeId.id(), 2L, 3L, 4L, null);
                Assert.fail("Should not have been able to download attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveResponse(null, projectCompositeId.id(), 2L, 3L, "", null, null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.saveResponse(null, projectCompositeId.id(), 2L, 3L, "", null, null, null, null, null, null);
                Assert.fail("Should not have been able to save response without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveResponseAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.saveNewResponseAttachment(null, projectCompositeId.id(), 2L, 3L, "", null, null, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.saveNewResponseAttachment(null, projectCompositeId.id(), 2L, 3L, "", null, null, null, null, null);
                Assert.fail("Should not have been able to save a response attachment without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testViewNewResponse() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.viewNewResponse(projectCompositeId.id(), 2L, 3L,"", null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.viewNewResponse(projectCompositeId.id(), 2L, 3L, "", null, null, null);
                Assert.fail("Should not have been able to show the add response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
    @Test
    public void testRemoveAttachment() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());
        assertSecured(() -> classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, "", 4L, null, null, null));

        List<Role> nonFinanceTeamRoles = asList(Role.values()).stream().filter(type ->type != PROJECT_FINANCE)
                .collect(toList());

        nonFinanceTeamRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.removeAttachment(projectCompositeId.id(), 2L, 3L, "", 4L, null, null, null);
                Assert.fail("Should not have been able to remove attachments from the create response form without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }
}
