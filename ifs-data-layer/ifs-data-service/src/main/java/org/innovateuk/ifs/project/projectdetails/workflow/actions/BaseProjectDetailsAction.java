package org.innovateuk.ifs.project.projectdetails.workflow.actions;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import java.util.Optional;

/**
 * A base class for Project-related workflow Actions
 */
public abstract class BaseProjectDetailsAction extends TestableTransitionWorkflowAction<ProjectDetailsState, ProjectDetailsEvent> {

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    public void doExecute(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {

        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        ProjectDetailsState newState = context.getTransition().getTarget().getId();

        doExecute(getProjectFromContext(context), getProjectDetailsFromContext(context), getProjectUserFromContext(context),
                newState, Optional.ofNullable(updatedProcessOutcome));
    }

    private Project getProjectFromContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {
        return (Project) context.getMessageHeader("project");
    }

    private ProjectDetailsProcess getProjectDetailsFromContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {
        return (ProjectDetailsProcess) context.getMessageHeader("projectDetails");
    }

    private ProjectUser getProjectUserFromContext(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {
        return (ProjectUser) context.getMessageHeader("projectUser");
    }

    protected void doExecute(Project project, ProjectDetailsProcess projectDetails, ProjectUser projectUser,
                                      ProjectDetailsState newState, Optional<ProcessOutcome> processOutcome) {

        projectDetails.setActivityState(newState);
        projectDetailsProcessRepository.save(projectDetails);
    }
}