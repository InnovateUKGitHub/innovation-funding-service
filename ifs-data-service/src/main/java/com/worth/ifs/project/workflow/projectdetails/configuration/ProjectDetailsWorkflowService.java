package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import com.worth.ifs.workflow.GenericPersistStateMachineHandler;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import org.springframework.messaging.support.MessageBuilder;

import static com.worth.ifs.project.resource.ProjectDetailsOutcomes.*;

/**
 * {@code ProjectDetailsWorkflowService} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
public class ProjectDetailsWorkflowService extends BaseWorkflowEventHandler<ProjectDetailsProcess, ProjectDetailsState, ProjectDetailsOutcomes> {

    public ProjectDetailsWorkflowService(GenericPersistStateMachineHandler<ProjectDetailsState, ProjectDetailsOutcomes> stateHandler, ProjectDetailsProcessRepository projectDetailsProcessRepository) {
        super(stateHandler, projectDetailsProcessRepository);
    }

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), ProjectDetailsState.PENDING);
    }

    public boolean projectDetailsAllSupplied(ProjectDetailsProcess projectDetails) {
        return fireEvent(allProjectDetailsSuppliedEvent(projectDetails), projectDetails);
    }

    public boolean submitProjectDetails(ProjectUser projectUser, Long projectId) {
        ProjectDetailsProcess process = getProcessByTargetId(projectId);
        return fireEvent(submitProjectDetailsMessage(projectUser, process), process);
    }

    public boolean isSubmissionAllowed(Long projectId) {
        ProjectDetailsProcess currentProcess = getProcessByTargetId(projectId);
        return testEvent(submitProjectDetailsMessage(currentProcess.getParticipant(), currentProcess), currentProcess);
    }

    private MessageBuilder<ProjectDetailsOutcomes> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("project", project)
                .setHeader("projectUser", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ProjectDetailsOutcomes> allProjectDetailsSuppliedEvent(ProjectDetailsProcess projectDetails) {
        return MessageBuilder
                .withPayload(READY_TO_SUBMIT)
                .setHeader("projectDetails", projectDetails);
    }

    private MessageBuilder<ProjectDetailsOutcomes> submitProjectDetailsMessage(ProjectUser projectUser, ProjectDetailsProcess projectDetails) {
        return MessageBuilder
                .withPayload(SUBMIT)
                .setHeader("projectDetails", projectDetails)
                .setHeader("projectUser", projectUser);
    }

    private boolean fireEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsProcess currentState) {
        return fireEvent(event, currentState.getActivityState());
    }

    private boolean fireEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsState currentState) {
        return stateHandler.handleEventWithState(event.build(), currentState);
    }

    private boolean testEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsProcess currentState) {
        return testEvent(event, currentState.getActivityState());
    }

    private boolean testEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsState currentState) {
        return fireEvent(event.setHeader(TestableTransitionWorkflowAction.TESTING_GUARD_KEY, true), currentState);
    }


}
