package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.core.transactional.ProjectServiceImpl;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class ProjectServiceSecurityTest extends BaseServiceSecurityTest<ProjectService> {

    private static final EnumSet<Role> NON_COMP_ADMIN_ROLES = complementOf(of(COMP_ADMIN, PROJECT_FINANCE));
    private static final EnumSet<Role> NON_SYSTEM_REGISTRATION_ROLES = complementOf(of(SYSTEM_REGISTRATION_USER));

    private ProjectPermissionRules projectPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(ProjectPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGetProjectById() {
        final Long projectId = 1L;

        when(classUnderTestMock.getProjectById(projectId))
                .thenReturn(serviceSuccess(newProjectResource().withId(projectId).build()));
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(newProjectResource().build());

        assertAccessDenied(
                () -> classUnderTest.getProjectById(projectId),
                () -> {
                    verify(projectPermissionRules, times(1))
                            .partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1))
                            .internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testCreateProjectFromApplicationAllowedIfCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.createProjectFromApplication(123L);
    }

    @Test
    public void testCreateProjectFromApplicationDeniedForApplicant() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build());
        try {
            classUnderTest.createProjectFromApplication(123L);
            fail("Should not have been able to create project from application as applicant");
        } catch (AccessDeniedException ade) {
            //expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
            Assert.fail("Should not have been able to create project from application without the global Comp Admin " +
                    "role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsDeniedIfNotCorrectGlobalRoles() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(role)).build());
            try {
                classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
                Assert.fail("Should not have been able to create project from application without the global Comp " +
                        "Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testGetProjectUsers() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectUsers(123L), () -> {
            verify(projectPermissionRules).partnersOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testAddPartnerDeniedIfNotSystemRegistrar() {
        NON_SYSTEM_REGISTRATION_ROLES.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.addPartner(1L, 2L, 3L);
                Assert.fail("Should not have been able to add a partner without the system registrar role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testAddPartnerAllowedIfSystemRegistrar() {
        Project project = newProject()
                .withId(1L)
                .build();

        ProjectResource projectResource = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        when(projectLookupStrategy.getProjectResource(project.getId())).thenReturn(projectResource);

        assertAccessDenied(
                () -> classUnderTest.addPartner(project.getId(), 1L, 1L),
                () -> {
                    verify(projectPermissionRules, times(1))
                            .systemRegistrarCanAddPartnersToProject(isA(ProjectResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(projectPermissionRules);
                }
        );
    }

    @Test
    public void
    test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRolesOrASystemRegistrar() {
        NON_SYSTEM_REGISTRATION_ROLES.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.addPartner(1L, 2L, 3L);
                Assert.fail("Should not have been able to add a partner without the system registrar role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testWithdrawProject() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.withdrawProject(123L),
                Role.IFS_ADMINISTRATOR);
    }

    @Override
    protected Class<? extends ProjectService> getClassUnderTest() {
        return ProjectServiceImpl.class;
    }
}

