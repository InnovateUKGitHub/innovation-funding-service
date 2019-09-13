package org.innovateuk.ifs.project.core.workflow.configuration.actions;

import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Project-related workflow Actions
 */
public abstract class BaseProjectAction extends TestableTransitionWorkflowAction<ProjectState, ProjectEvent> {

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Override
    public void doExecute(StateContext<ProjectState, ProjectEvent> context) {
        Project project = getProjectFromContext(context);
        doExecute(project, context);
    }

    private Project getProjectFromContext(StateContext<ProjectState, ProjectEvent> context) {
        return (Project) context.getMessageHeader("target");
    }

    protected abstract void doExecute(Project project, StateContext<ProjectState, ProjectEvent>  context);
}
