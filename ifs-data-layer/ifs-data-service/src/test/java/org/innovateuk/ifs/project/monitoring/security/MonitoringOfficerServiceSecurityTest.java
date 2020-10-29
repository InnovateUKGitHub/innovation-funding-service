package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class MonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<MonitoringOfficerService> {
    private MonitoringOfficerPermissionRules rules;
    private UserLookupStrategies lookup;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(MonitoringOfficerPermissionRules.class);
        lookup = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }
    @Test
    public void findAllProjectMonitoringOfficersOnlyIfGlobaLCompAdminOrProjectFinanceOrIfsAdminRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.findAll(),
                                                COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR);
    }

    @Test
    public void getProjectMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceOrIfsAdminRole() {
        final long userId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getProjectMonitoringOfficer(userId),
                                                COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR);
    }

    @Test
    public void assignProjectToMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceOrIfsAdminRole() {
        final long userId = 1;
        final long projectId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.assignProjectToMonitoringOfficer(userId, projectId),
                                                COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR);
    }

    @Test
    public void unassignProjectToMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceOrIfsAdminRole() {
        final long userId = 1;
        final long projectId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.unassignProjectFromMonitoringOfficer(userId, projectId),
                                                COMP_ADMIN, PROJECT_FINANCE, IFS_ADMINISTRATOR);
    }

    @Test
    public void getMonitoringOfficerProjects() {
        UserResource user = newUserResource().build();
        when(lookup.findById(user.getId())).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.getMonitoringOfficerProjects(user.getId()), () -> {
            verify(rules).internalUsersCanSeeMonitoringOfficerProjects(user, getLoggedInUser());
            verify(rules).monitoringOfficerCanSeeTheirOwnProjects(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getMonitoringOfficer() {
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.findMonitoringOfficerForProject(123L), () -> {
            verify(rules).internalUsersCanViewMonitoringOfficersForAnyProject(project, getLoggedInUser());
            verify(rules).partnersCanViewMonitoringOfficersOnTheirProjects(project, getLoggedInUser());
            verify(rules).stakeholdersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(project, getLoggedInUser());
            verify(rules).monitoringOfficersCanViewThemselves(project, getLoggedInUser());
            verify(rules).competitionFinanceUsersCanViewMonitoringOfficersForAProjectOnTheirCompetitions(project, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void isMonitoringOfficer() {
        UserResource user = newUserResource().build();
        when(lookup.findById(user.getId())).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.isMonitoringOfficer(user.getId()), () -> {
            verify(rules).usersCanSeeIfTheyAreMonitoringOfficerOnProjects(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Override
    protected Class<? extends MonitoringOfficerService> getClassUnderTest() {
        return MonitoringOfficerServiceImpl.class;
    }
}

