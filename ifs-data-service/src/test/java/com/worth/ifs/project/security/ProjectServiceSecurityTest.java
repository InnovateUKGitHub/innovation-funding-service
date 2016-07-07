package com.worth.ifs.project.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.transactional.ProjectService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.APPLICANT;
import static com.worth.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
                () -> service.getProjectById(projectId),
                () -> {
                    verify(projectPermissionRules, times(1)).partnersOnProjectCanView(isA(ProjectResource.class), isA(UserResource.class));
                    verify(projectPermissionRules, times(1)).compAdminsCanViewProjects(isA(ProjectResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testCreateProjectFromApplicationAllowedIfCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN).build())).build());
        service.createProjectFromApplication(123L);
    }

    @Test
    public void testCreateProjectFromApplicationDeniedForApplicant() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(APPLICANT).build())).build());
        try {
            service.createProjectFromApplication(123L);
            fail("Should not have been able to create project from application as applicant");
        } catch(AccessDeniedException ade){
            //expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfGlobalCompAdminRole() {
        RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        service.createProjectsFromFundingDecisions(new HashMap<>());
    }

    @Test
    public void testCreateProjectFromFundingDecisionsAllowedIfNoGlobalRolesAtAll() {
        try {
            service.createProjectsFromFundingDecisions(new HashMap<>());
            Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testCreateProjectFromFundingDecisionsDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                service.createProjectsFromFundingDecisions(new HashMap<>());
                Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testUpdateProjectStartDate() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.updateProjectStartDate(123L, LocalDate.now()), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testUpdateProjectAddress() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(456L)).thenReturn(project);

        assertAccessDenied(() -> service.updateProjectAddress(123L, 456L, OrganisationAddressType.ADD_NEW, newAddressResource().build()), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testUpdateFinanceContact() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.updateFinanceContact(123L, 456L, 789L), () -> {
            verify(projectPermissionRules).partnersCanUpdateTheirOwnOrganisationsFinanceContacts(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSetProjectManager() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.setProjectManager(123L, 456L), () -> {
            verify(projectPermissionRules).leadPartnersCanUpdateTheBasicProjectDetails(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetProjectUsers() {

        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.getProjectUsers(123L), () -> {
            verify(projectPermissionRules).partnersOnProjectCanView(project, getLoggedInUser());
            verify(projectPermissionRules).compAdminsCanViewProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testGetMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.getMonitoringOfficer(123L), () -> {
            verify(projectPermissionRules).compAdminsCanViewMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verify(projectPermissionRules).partnersCanViewMonitoringOfficersOnTheirProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSaveMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> service.saveMonitoringOfficer(123L, newMonitoringOfficerResource().build()), () -> {
            verify(projectPermissionRules).compAdminsCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Override
    protected Class<TestProjectService> getServiceClass() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements ProjectService {

        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

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
		public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
			return null;
		}

		@Override
        public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateProjectAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource projectAddress) {
            return null;
        }

        @Override
        public ServiceResult<List<ProjectResource>> findByUserId(Long userId) {
            return null;
        }

		@Override
		public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
			return null;
		}

        @Override
        public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
            return serviceSuccess(newProjectUserResource().build(2));
        }

        @Override
        public ServiceResult<Void> saveProjectSubmitDateTime(Long id, LocalDateTime date) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
            return null;
        }

		@Override
        public ServiceResult<Void> saveMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {
            return null;
        }

        @Override
        public ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(Long projectId, Long userId) {
            return null;
        }

        @Override
        public ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId) {
            return null;
        }
    }
}

