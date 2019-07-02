package org.innovateuk.ifs.project.projectdetails.workflow.actions;

import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsCompleteActionTest {

    @InjectMocks
    private ProjectDetailsCompleteAction projectDetailsCompleteAction;

    @Mock
    private ActivityLogService activityLogService;

    @Test
    public void doExecute() {
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();
        ProjectDetailsState state = ProjectDetailsState.SUBMITTED;

        projectDetailsCompleteAction.doExecute(project, projectUser, state);

        verify(activityLogService).recordActivityByProjectId(project.getId(), ActivityType.PROJECT_DETAILS_COMPLETE);
    }
}
