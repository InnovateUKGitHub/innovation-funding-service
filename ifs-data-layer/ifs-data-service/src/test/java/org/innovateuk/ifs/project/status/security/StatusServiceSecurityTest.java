package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.innovateuk.ifs.project.status.transactional.StatusServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class StatusServiceSecurityTest extends BaseServiceSecurityTest<StatusService> {

    private StatusPermissionRules statusPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;
    private CompetitionLookupStrategy competitionLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        statusPermissionRules = getMockPermissionRulesBean(StatusPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
    }

    @Test
    public void getProjectTeamStatus(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectTeamStatus(123L, Optional.empty()), () -> {
            verify(statusPermissionRules).partnersCanViewTeamStatus(project, getLoggedInUser());
            verify(statusPermissionRules).internalUsersCanViewTeamStatus(project, getLoggedInUser());
            verify(statusPermissionRules).stakeholdersCanViewTeamStatus(project, getLoggedInUser());
            verify(statusPermissionRules).monitoringOfficersCanViewTeamStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(statusPermissionRules);
        });
    }

    @Override
    protected Class<? extends StatusService> getClassUnderTest() {
        return StatusServiceImpl.class;
    }
}