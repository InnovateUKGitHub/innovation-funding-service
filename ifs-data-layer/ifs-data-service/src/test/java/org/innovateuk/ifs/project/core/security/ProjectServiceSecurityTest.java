package org.innovateuk.ifs.project.core.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.core.transactional.ProjectServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
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
        final long projectId = 1L;

        when(classUnderTestMock.getProjectById(projectId)).thenReturn(serviceSuccess(newProjectResource().withId(projectId).build()));
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
    public void createProjectFromApplication_allowedIfCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.createProjectFromApplication(123L);
    }

    @Test
    public void createProjectFromApplication_deniedForApplicant() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build());
        try {
            classUnderTest.createProjectFromApplication(123L);
            fail("Should not have been able to create project from application as applicant");
        } catch (AccessDeniedException ade) {
            //expected behaviour
        }
    }

    @Test
    public void createProjectFromFundingDecisions_allowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
    }

    @Test
    public void createProjectFromFundingDecisions_allowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
            Assert.fail("Should not have been able to create project from application without the global Comp Admin " +
                    "role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void createProjectFromFundingDecisions_deniedIfNotCorrectGlobalRoles() {
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
    public void getProjectUsers() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectUsersByProjectIdAndRoleIn(123L, PROJECT_USER_ROLES.stream().collect(Collectors.toList())), () -> {
            verify(projectPermissionRules).partnersOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewProjects(project, getLoggedInUser());
            verify(projectPermissionRules).monitoringOfficerOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).stakeholdersCanViewProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void existsOnApplication() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(1L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.existsOnApplication(1L,2L ), () -> {
            verify(projectPermissionRules).partnersOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).internalUsersCanViewProjects(project, getLoggedInUser());
            verify(projectPermissionRules).monitoringOfficerOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).stakeholdersCanViewProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void addPartner_deniedIfNotSystemRegistrar() {
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
    public void addPartner_allowedIfSystemRegistrar() {
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
    public void createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRolesOrASystemRegistrar() {
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

    @Override
    protected Class<? extends ProjectService> getClassUnderTest() {
        return ProjectServiceImpl.class;
    }
}

