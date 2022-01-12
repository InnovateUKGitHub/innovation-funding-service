package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SetupSectionsPermissionRulesTest extends BasePermissionRulesTest<SetupSectionsPermissionRules> {

    @Mock
    private ProjectService projectService;

    private UserResource monitoringOfficer = newUserResource().withRoleGlobal(Role.MONITORING_OFFICER).build();

    private CompetitionResource competition = newCompetitionResource().build();

    private ApplicationResource application = newApplicationResource()
            .withCompetition(competition.getId())
            .build();

    private ProjectResource activeProject = newProjectResource()
            .withProjectState(ProjectState.SETUP)
            .withApplication(application)
            .withMonitoringOfficerUser(monitoringOfficer.getId())
            .build();

    @Override
    protected SetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new SetupSectionsPermissionRules();
    }

    @Test
    public void projectMoCanAccessSpendProfileSection() {
        activeProject.setMonitoringOfficerUser(monitoringOfficer.getId());
        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        ProjectCompositeId projectCompositeId = ProjectCompositeId.id(activeProject.getId());

        assertTrue(rules.projectMoCanAccessSpendProfileSection(projectCompositeId, monitoringOfficer));

        verify(projectService).getById(activeProject.getId());
    }
}
