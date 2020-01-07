package org.innovateuk.ifs.project.core.workflow.configuration.guard;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code ProjectInSetupGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class ProjectInSetupGuard implements Guard<ProjectState, ProjectEvent> {

    @Override
    public boolean evaluate(StateContext<ProjectState, ProjectEvent> context) {
        Project project = (Project) context.getMessageHeader("target");
        return isProjectValid(project);
    }

    private boolean isProjectValid(Project project) {
        return project.getProjectProcess().getProcessState() != ProjectState.WITHDRAWN;
    }
}