package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * {@code ProjectDetailsWorkflowEventHandler} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
public class ProjectDetailsWorkflowEventHandler extends BaseWorkflowEventHandler {

    public ProjectDetailsWorkflowEventHandler(PersistStateMachineHandler stateHandler) {
        super(stateHandler);
    }

    public boolean projectCreated(Project project, Long originalLeadApplicantProjectUserId) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.PENDING.getType())
                .setHeader("project", project)
                .setHeader("projectUserId", originalLeadApplicantProjectUserId)
                .build(), ProjectDetailsState.PENDING.name());
    }

    public boolean projectDetailsAllSupplied(ProjectDetailsProcess projectDetails) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.READY_TO_SUBMIT.getType())
                .setHeader("projectDetails", projectDetails)
                .build(), projectDetails.getActivityState().getBackingState().name());
    }

    public boolean submitProjectDetails(Long projectUserId, ProjectDetailsProcess projectDetails) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.SUBMIT.getType())
                .setHeader("projectDetails", projectDetails)
                .setHeader("projectUserId", projectUserId)
                .build(), projectDetails.getActivityState().getBackingState().name());
    }
}
