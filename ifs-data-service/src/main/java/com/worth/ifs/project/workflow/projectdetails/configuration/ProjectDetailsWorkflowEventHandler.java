package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * {@code ProjectDetailsWorkflowEventHandler} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
public class ProjectDetailsWorkflowEventHandler extends BaseWorkflowEventHandler<ProjectDetailsProcess> {

    public ProjectDetailsWorkflowEventHandler(PersistStateMachineHandler stateHandler, ProjectDetailsProcessRepository projectDetailsProcessRepository) {
        super(stateHandler, projectDetailsProcessRepository);
    }

    public boolean projectCreated(Project project, Long originalLeadApplicantProjectUserId) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUserId), ProjectDetailsState.PENDING.name());
    }

    public boolean projectDetailsAllSupplied(ProjectDetailsProcess projectDetails) {
        return fireEvent(allProjectDetailsSuppliedEvent(projectDetails), projectDetails);
    }

    public boolean submitProjectDetails(Long projectUserId, Long projectId) {
        ProjectDetailsProcess process = getProcessByTargetId(projectId);
        return fireEvent(submitProjectDetailsMessage(projectUserId, process), process);
    }

    public boolean isSubmissionAllowed(Long projectId) {
        ProjectDetailsProcess currentProcess = getProcessByTargetId(projectId);
        return testEvent(submitProjectDetailsMessage(currentProcess.getParticipant().getId(), currentProcess), currentProcess);
    }

    private MessageBuilder<String> projectCreatedEvent(Project project, Long originalLeadApplicantProjectUserId) {
        return MessageBuilder
                .withPayload(ProjectDetailsOutcomes.PENDING.getType())
                .setHeader("project", project)
                .setHeader("projectUserId", originalLeadApplicantProjectUserId);
    }

    private MessageBuilder<String> allProjectDetailsSuppliedEvent(ProjectDetailsProcess projectDetails) {
        return MessageBuilder
                .withPayload(ProjectDetailsOutcomes.READY_TO_SUBMIT.getType())
                .setHeader("projectDetails", projectDetails);
    }

    private MessageBuilder<String> submitProjectDetailsMessage(Long projectUserId, ProjectDetailsProcess projectDetails) {
        return MessageBuilder
                .withPayload(ProjectDetailsOutcomes.SUBMIT.getType())
                .setHeader("projectDetails", projectDetails)
                .setHeader("projectUserId", projectUserId);
    }

    private boolean fireEvent(MessageBuilder<String> event, ProjectDetailsProcess currentState) {
        return fireEvent(event, currentState.getActivityState().getBackingState().name());
    }

    private boolean fireEvent(MessageBuilder<String> event, String currentState) {
        return stateHandler.handleEventWithState(event.build(), currentState);
    }

    private boolean testEvent(MessageBuilder<String> event, ProjectDetailsProcess currentState) {
        return testEvent(event, currentState.getActivityState().getBackingState().name());
    }

    private boolean testEvent(MessageBuilder<String> event, String currentState) {
        return fireEvent(event.setHeader(TestableTransitionWorkflowAction.TESTING_GUARD_KEY, true), currentState);
    }


}
