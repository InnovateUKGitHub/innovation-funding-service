package org.innovateuk.ifs.project.projectdetails.workflow.actions;

import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectDetailsCompleteAction extends BaseProjectDetailsAction {

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    protected void doExecute(Project project, ProjectUser projectUserFromContext, ProjectDetailsState newState) {
        activityLogService.recordActivityByProjectId(project.getId(), ActivityType.PROJECT_DETAILS_COMPLETE);
    }
}
