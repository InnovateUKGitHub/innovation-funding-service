package com.worth.ifs.project.workflow.projectdetails.actions;

import com.worth.ifs.assessment.domain.Project;
import com.worth.ifs.assessment.repository.ProjectRepository;
import com.worth.ifs.assessment.repository.ProcessOutcomeRepository;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.repository.ProjectRepository;
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
    protected ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Autowired
    protected ProcessOutcomeRepository processOutcomeRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Override
    public void execute(StateContext<String, String> context) {

        Project assessment = getProjectFromContext(context);
        ProcessOutcome updatedProcessOutcome = (ProcessOutcome) context.getMessageHeader("processOutcome");
        State newState = State.valueOf(context.getTransition().getTarget().getId());

        ActivityState newActivityState = activityStateRepository.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, newState);
        doExecute(assessment, newActivityState, Optional.ofNullable(updatedProcessOutcome));
    }

    private Project getProjectFromContext(StateContext<String, String> context) {

        Project assessmentInContext = (Project) context.getMessageHeader("assessment");

        if (assessmentInContext != null) {
            return assessmentInContext;
        } else {
            Long projectUserId = (Long) context.getMessageHeader("projectUserId");
            return projectDetailsProcessRepository.findOneByParticipantId(projectUserId);
        }
    }

    protected abstract void doExecute(Project assessment, ActivityState newState, Optional<ProcessOutcome> processOutcome);
}
