package org.innovateuk.ifs.project.core.workflow.configuration.guard;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * {@code ProjectInSetupGuard} is responsible for testing if the transition can take place
 * to the next state. This will not happen if the evaluation is failing.
 */
@Component
public class ProjectInSetupGuard implements Guard<ProjectState, ProjectEvent> {

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Override
    public boolean evaluate(StateContext<ProjectState, ProjectEvent> context) {
        Project project = (Project) context.getMessageHeader("target");
        return isProjectValid(project);
    }

    private boolean isProjectValid(Project project) {
        ProjectProcess projectProcess = projectProcessRepository.findOneByTargetId(project.getId());
        return projectProcess.getProcessState() != ProjectState.WITHDRAWN;
    }
}