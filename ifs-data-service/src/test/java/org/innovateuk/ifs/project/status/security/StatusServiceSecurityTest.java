package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in ProjectService interact with Spring Security
 */
public class StatusServiceSecurityTest extends BaseServiceSecurityTest<StatusService> {

    private StatusPermissionRules statusPermissionRules;
    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        statusPermissionRules = getMockPermissionRulesBean(StatusPermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGetProjectTeamStatus(){
        ProjectResource project = newProjectResource().build();

        when(projectLookupStrategy.getProjectResource(123L)).thenReturn(project);

        assertAccessDenied(() -> classUnderTest.getProjectTeamStatus(123L, Optional.empty()), () -> {
            verify(statusPermissionRules).partnersCanViewTeamStatus(project, getLoggedInUser());
            verify(statusPermissionRules).internalUsersCanViewTeamStatus(project, getLoggedInUser());
            verifyNoMoreInteractions(statusPermissionRules);
        });
    }

    @Override
    protected Class<TestProjectService> getClassUnderTest() {
        return TestProjectService.class;
    }

    public static class TestProjectService implements StatusService {

        @Override
        public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId) {
            return null;
        }
    }
}


