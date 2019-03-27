package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class ProjectMonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<ProjectMonitoringOfficerService> {

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

    @Override
    protected Class<? extends ProjectMonitoringOfficerService> getClassUnderTest() {
        return ProjectMonitoringOfficerServiceImpl.class;
    }
}

