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

    public boolean projectStartDateAdded(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, PROJECT_START_DATE_ADDED), project);
    }

    public boolean projectAddressAdded(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, PROJECT_ADDRESS_ADDED), project);
    }

    public boolean projectManagerAdded(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, PROJECT_MANAGER_ADDED), project);
    }

    public boolean submitProjectDetails(ProjectUser projectUser, Project project) {
        return fireEvent(submitProjectDetailsMessage(projectUser, project), project);
    }

    public boolean isSubmissionAllowed(Project project) {
        return testEvent(submitProjectDetailsMessage(null, project), project);
    }

    private MessageBuilder<ProjectDetailsOutcomes> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("project", project)
                .setHeader("projectUser", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ProjectDetailsOutcomes> mandatoryValueAddedEvent(Project project, ProjectUser projectUser,
                                                                            ProjectDetailsOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("project", project)
                .setHeader("projectUser", projectUser);
    }

    private MessageBuilder<ProjectDetailsOutcomes> submitProjectDetailsMessage(ProjectUser projectUser, Project project) {
        return MessageBuilder
                .withPayload(SUBMIT)
                .setHeader("project", project)
                .setHeader("projectUser", projectUser);
    }

    private boolean fireEvent(MessageBuilder<ProjectDetailsOutcomes> event, Project project) {
        return fireEvent(event, getCurrentProcess(project));
    }

    private boolean fireEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsProcess currentState) {
        return fireEvent(event, currentState.getActivityState());
    }

    private boolean fireEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsState currentState) {
        return stateHandler.handleEventWithState(event.build(), currentState);
    }

    private boolean testEvent(MessageBuilder<ProjectDetailsOutcomes> event, Project project) {
        return testEvent(event, getCurrentProcess(project).getActivityState());
    }

    private boolean testEvent(MessageBuilder<ProjectDetailsOutcomes> event, ProjectDetailsState currentState) {
        return fireEvent(event.setHeader(TestableTransitionWorkflowAction.TESTING_GUARD_KEY, true), currentState);
    }

    private ProjectDetailsProcess getCurrentProcess(Project project) {
        return processRepository.findOneByTargetId(project.getId());
    }
}
