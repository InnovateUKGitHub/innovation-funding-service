package com.worth.ifs.project.workflow.projectdetails.actions;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;

import java.util.Optional;

import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;

/**
 * A base class for Project-related workflow Actions
 */
abstract class BaseProjectDetailsAction extends TestableTransitionWorkflowAction<ProjectDetailsState, ProjectDetailsOutcomes> {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    public void doExecute(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {

        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = context.getTransition().getTarget().getId().getBackingState();

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);
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
