package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LegacyMonitoringOfficerPermissionRulesTest extends BasePermissionRulesTest<LegacyMonitoringOfficerPermissionRules> {
    private ProjectProcess projectProcess;

    @Mock
    private ProjectProcessRepository projectProcessRepository;

    @Before
    public void setUp() throws Exception {
        projectProcess = newProjectProcess().withActivityState(SETUP).build();
    }

    @Override
    protected LegacyMonitoringOfficerPermissionRules supplyPermissionRulesUnderTest() {
        return new LegacyMonitoringOfficerPermissionRules();
    }

    @Test
    public void internalUsersCanEditMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource()
                .withProjectState(SETUP)
                .build();

        when(projectProcessRepository.findOneByTargetId(project.getId())).thenReturn(projectProcess);

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            }
        });
    }
}
