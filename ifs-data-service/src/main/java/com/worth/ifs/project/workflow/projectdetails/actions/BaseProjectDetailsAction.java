package com.worth.ifs.project.workflow.projectdetails.actions;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.Optional;

import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;

/**
 * A base class for Project-related workflow Actions
 */
abstract class BaseProjectDetailsAction implements Action<String, String> {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    public void execute(StateContext<String, String> context) {

        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = State.valueOf(context.getTransition().getTarget().getId());

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);
        doExecute(getProjectFromContext(context), getProjectDetailsFromContext(context), getProjectUserFromContext(context),
                newActivityState, Optional.ofNullable(updatedProcessOutcome));
    }

    private Project getProjectFromContext(StateContext<String, String> context) {
        return (Project) context.getMessageHeader("project");
    }

    private ProjectDetailsProcess getProjectDetailsFromContext(StateContext<String, String> context) {
        return (ProjectDetailsProcess) context.getMessageHeader("projectDetails");
    }

    private ProjectUser getProjectUserFromContext(StateContext<String, String> context) {
        return (ProjectUser) context.getMessageHeader("projectUser");
    }

    protected void doExecute(Project project, ProjectDetailsProcess projectDetails, ProjectUser projectUser,
                                      ActivityState newState, Optional<ProcessOutcome> processOutcome) {

        projectDetails.setActivityState(newState);
        projectDetailsProcessRepository.save(projectDetails);
    }
}
