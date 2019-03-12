package org.innovateuk.ifs.project.monitoring.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerServiceImpl;
import org.junit.Test;


import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * Testing how the secured methods in ProjectMonitoringOfficerService interact with Spring Security
 */
public class ProjectMonitoringOfficerServiceSecurityTest extends BaseServiceSecurityTest<ProjectMonitoringOfficerService> {


    @Test
    public void testGetProjectMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        final long userId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getProjectMonitoringOfficer(userId), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void testAssignProjectToMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        final long userId = 1;
        final long projectId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.assignProjectToMonitoringOfficer(userId, projectId), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void testUnassignProjectToMonitoringOfficerOnlyIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        final long userId = 1;
        final long projectId = 1;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.unassignProjectFromMonitoringOfficer(userId, projectId), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Override
    protected Class<? extends ProjectMonitoringOfficerService> getClassUnderTest() {
        return ProjectMonitoringOfficerServiceImpl.class;
    }
}

