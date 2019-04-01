package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class LegacyMonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<LegacyMonitoringOfficerService> {

    private LegacyMonitoringOfficerPermissionRules permissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(LegacyMonitoringOfficerPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void getMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getMonitoringOfficer(123L), () -> {
            verify(permissionRules).internalUsersCanViewMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verify(permissionRules).partnersCanViewMonitoringOfficersOnTheirProjects(project, getLoggedInUser());
            verify(permissionRules).stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(project, getLoggedInUser());
            verify(permissionRules).monitoringOfficersCanViewThemselves(project, getLoggedInUser());
            verifyNoMoreInteractions(permissionRules);
        });
    }

    @Test
    public void saveMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.saveMonitoringOfficer(123L, newLegacyMonitoringOfficerResource().build()), () -> {
            verify(permissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verifyNoMoreInteractions(permissionRules);
        });
    }

    @Test
    public void notifyMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.notifyStakeholdersOfMonitoringOfficerChange(newLegacyMonitoringOfficerResource().withProject(123L).build()),
                () -> {
                    verify(permissionRules).internalUsersCanAssignMonitoringOfficersForAnyProject(project, getLoggedInUser());
                    verifyNoMoreInteractions(permissionRules);
                });
    }

    @Override
    protected Class<? extends LegacyMonitoringOfficerService> getClassUnderTest() {
        return LegacyMonitoringOfficerServiceImpl.class;
    }
}
