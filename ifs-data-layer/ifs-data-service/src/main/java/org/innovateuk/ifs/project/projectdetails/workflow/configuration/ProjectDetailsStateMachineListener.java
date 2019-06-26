package org.innovateuk.ifs.project.projectdetails.workflow.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.activitylog.domain.ActivityType.PROJECT_DETAILS_COMPLETE;
import static org.innovateuk.ifs.project.resource.ProjectDetailsState.SUBMITTED;
import static org.springframework.statemachine.StateContext.Stage.STATE_CHANGED;

@Component
public class ProjectDetailsStateMachineListener extends StateMachineListenerAdapter<ProjectDetailsState, ProjectDetailsEvent> {
    private static final Log LOG = LogFactory.getLog(ProjectDetailsStateMachineListener.class);

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public void stateContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> stateContext) {
        if (STATE_CHANGED.equals(stateContext.getStage())) {
            if (stateContext.getTarget().getId().equals(SUBMITTED)) {
                Project project = (Project) stateContext.getMessageHeader("target");
                activityLogService.recordActivityByProjectId(project.getId(), PROJECT_DETAILS_COMPLETE);
            }
        }

    }

}
