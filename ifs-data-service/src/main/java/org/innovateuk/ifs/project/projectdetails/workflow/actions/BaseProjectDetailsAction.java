package org.innovateuk.ifs.project.projectdetails.workflow.actions;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.projectdetails.domain.ProjectDetailsProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.projectdetails.repository.ProjectDetailsProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectDetailsOutcomes;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import java.util.Optional;

import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;

/**
 * A base class for Project-related workflow Actions
 */
public abstract class BaseProjectDetailsAction extends TestableTransitionWorkflowAction<ProjectDetailsState, ProjectDetailsOutcomes> {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    public void doExecute(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {

        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = context.getTransition().getTarget().getId().getBackingState();

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(PROJECT_SETUP_PROJECT_DETAILS, newState);
        doExecute(getProjectFromContext(context), getProjectDetailsFromContext(context), getProjectUserFromContext(context),
                newActivityState, Optional.ofNullable(updatedProcessOutcome));
    }

    private Project getProjectFromContext(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {
        return (Project) context.getMessageHeader("project");
    }

    private ProjectDetailsProcess getProjectDetailsFromContext(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {
        return (ProjectDetailsProcess) context.getMessageHeader("projectDetails");
    }

    private ProjectUser getProjectUserFromContext(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {
        return (ProjectUser) context.getMessageHeader("projectUser");
    }

    protected void doExecute(Project project, ProjectDetailsProcess projectDetails, ProjectUser projectUser,
                                      ActivityState newState, Optional<ProcessOutcome> processOutcome) {

        projectDetails.setActivityState(newState);
        projectDetailsProcessRepository.save(projectDetails);
    }
}
