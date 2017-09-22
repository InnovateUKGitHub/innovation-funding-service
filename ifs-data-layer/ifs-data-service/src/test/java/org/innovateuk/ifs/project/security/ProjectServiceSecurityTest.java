package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.util.*;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class ProjectServiceSecurityTest extends BaseServiceSecurityTest<ProjectService> {

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

        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(newProjectResource().build());

        assertAccessDenied(
                () -> classUnderTest.getProjectById(projectId),
                () -> {
                    verify(projectPermissionRules, times(1)).partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1)).internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testCreateProjectFromApplicationAllowedIfCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
        classUnderTest.createProjectFromApplication(123L);
    }

    @Test
    public void testCreateProjectFromApplicationDeniedForApplicant() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(APPLICANT).build())).build());
        try {
            classUnderTest.createProjectFromApplication(123L);
            fail("Should not have been able to create project from application as applicant");
        } catch(AccessDeniedException ade){
            //expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfGlobalCompAdminRole() {
        RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
            Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.createProjectsFromFundingDecisions(new HashMap<>());
                Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
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
        EnumSet<UserRoleType> nonSystemRegistrationRoles = complementOf(of(SYSTEM_REGISTRATION_USER));
        nonSystemRegistrationRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(SYSTEM_REGISTRATION_USER).build())).build());
        classUnderTest.addPartner(1L, 2L, 3L);
        // There should be no exception thrown
    }

    @Test
    public void test_createApplicationByAppNameForUserIdAndCompetitionId_deniedIfNotCorrectGlobalRolesOrASystemRegistrar() {
        EnumSet<UserRoleType> nonSystemRegistrationRoles = complementOf(of(SYSTEM_REGISTRATION_USER));
        nonSystemRegistrationRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                classUnderTest.addPartner(1L, 2L, 3L);
                Assert.fail("Should not have been able to add a partner without the system registrar role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testGetProjectManager(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);
        assertAccessDenied(
                () -> classUnderTest.getProjectById(123L),
                () -> {
                    verify(projectPermissionRules, times(1)).partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1)).internalUsersCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(projectPermissionRules);
                }
        );
    }

    @Override
    protected Class<TestProjectService> getClassUnderTest() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements ProjectService {

        @Override
        public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
            return serviceSuccess(newProjectResource().withId(1L).build());
        }

        @Override
        public ServiceResult<ProjectResource> getByApplicationId(@P("applicationId") Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectResource>> findAll() {
            return null;
        }

        @Override
        public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectResource>> findByUserId(Long userId) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
            return serviceSuccess(newProjectUserResource().build(2));
        }

        @Override
        public ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectUser> addPartner(Long projectId, Long userId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectUserResource> getProjectManager(Long projectId) {
            return serviceSuccess(newProjectUserResource().withProject(projectId).withRoleName("project-manager").build());
        }
    }
}

