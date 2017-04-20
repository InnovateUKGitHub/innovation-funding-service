package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.monitoringofficer.transactional.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.security.ProjectPermissionRules;
import org.innovateuk.ifs.project.transactional.SaveMonitoringOfficerResult;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class ProjectMonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<ProjectMonitoringOfficerService> {

    private ProjectPermissionRules projectPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        projectPermissionRules = getMockPermissionRulesBean(ProjectPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGetMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getMonitoringOfficer(123L), () -> {
            verify(projectPermissionRules).internalUsersCanViewMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verify(projectPermissionRules).partnersCanViewMonitoringOfficersOnTheirProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testSaveMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.saveMonitoringOfficer(123L, newMonitoringOfficerResource().build()), () -> {
            verify(projectPermissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verifyNoMoreInteractions(projectPermissionRules);
        });
    }

    @Test
    public void testNotifyMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.notifyStakeholdersOfMonitoringOfficerChange(newMonitoringOfficerResource().withProject(123L).build()),
                () -> {
                    verify(projectPermissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
                    verifyNoMoreInteractions(projectPermissionRules);
                });
    }

    @Override
    protected Class<TestProjectService> getClassUnderTest() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements ProjectMonitoringOfficerService {

        @Override
        public ServiceResult<SaveMonitoringOfficerResult> saveMonitoringOfficer(final Long projectId, final MonitoringOfficerResource monitoringOfficerResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> notifyStakeholdersOfMonitoringOfficerChange(MonitoringOfficerResource monitoringOfficer) {
            return null;
        }

        @Override
        public ServiceResult<MonitoringOfficerResource> getMonitoringOfficer(Long projectId) {
            return null;
        }
    }
}
