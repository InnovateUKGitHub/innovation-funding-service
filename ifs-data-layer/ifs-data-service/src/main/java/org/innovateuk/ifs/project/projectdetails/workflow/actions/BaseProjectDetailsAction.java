package org.innovateuk.ifs.project.projectdetails.workflow.actions;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

/**
 * A base class for Project-related workflow Actions
 */
public abstract class BaseProjectDetailsAction extends TestableTransitionWorkflowAction<ProjectDetailsState, ProjectDetailsEvent> {

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    public void doExecute(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {

        ProjectDetailsState newState = context.getTransition().getTarget().getId();

        doExecute(getProjectFromContext(context), getProjectUserFromContext(context),
                newState);
    }

    protected abstract void doExecute(Project projectFromContext, ProjectUser projectUserFromContext, ProjectDetailsState newState);

    private Project getProjectFromContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {
        return (Project) context.getMessageHeader("target");
    }

    private ProjectUser getProjectUserFromContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {
        return (ProjectUser) context.getMessageHeader("participant");
    }

}