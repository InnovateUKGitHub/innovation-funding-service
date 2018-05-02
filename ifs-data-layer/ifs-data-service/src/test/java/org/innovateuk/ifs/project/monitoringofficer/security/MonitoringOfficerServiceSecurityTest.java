package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.monitoringofficer.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.MonitoringOfficerServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class MonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<MonitoringOfficerService> {

    private MonitoringOfficerPermissionRules permissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(MonitoringOfficerPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGetMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getMonitoringOfficer(123L), () -> {
            verify(permissionRules).internalUsersCanViewMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verify(permissionRules).partnersCanViewMonitoringOfficersOnTheirProjects(project, getLoggedInUser());
            verifyNoMoreInteractions(permissionRules);
        });
    }

    @Test
    public void testSaveMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.saveMonitoringOfficer(123L, newMonitoringOfficerResource().build()), () -> {
            verify(permissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verifyNoMoreInteractions(permissionRules);
        });
    }

    @Test
    public void testNotifyMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.notifyStakeholdersOfMonitoringOfficerChange(newMonitoringOfficerResource().withProject(123L).build()),
                () -> {
                    verify(permissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
                    verifyNoMoreInteractions(permissionRules);
                });
    }

    @Override
    protected Class<? extends MonitoringOfficerService> getClassUnderTest() {
        return MonitoringOfficerServiceImpl.class;
    }
}
