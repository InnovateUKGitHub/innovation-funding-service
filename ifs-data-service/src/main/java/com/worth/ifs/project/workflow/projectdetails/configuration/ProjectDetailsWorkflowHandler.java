package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.BaseWorkflowEventHandler;
import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import static com.worth.ifs.project.resource.ProjectDetailsOutcomes.*;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_PROJECT_DETAILS;

/**
 * {@code ProjectDetailsWorkflowService} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
@Component
public class ProjectDetailsWorkflowHandler extends BaseWorkflowEventHandler<ProjectDetailsProcess, ProjectDetailsState, ProjectDetailsOutcomes, Project, ProjectUser> {

    @Autowired
    @Qualifier("projectDetailsStateMachine")
    private StateMachine<ProjectDetailsState, ProjectDetailsOutcomes> stateMachine;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

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

    public boolean projectFinanceContactAdded(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, PROJECT_FINANCE_CONTACT_ADDED), project);
    }

    public boolean submitProjectDetails(Project project, ProjectUser projectUser) {
        return fireEvent(submitProjectDetailsMessage(projectUser, project), project);
    }

    public boolean isSubmissionAllowed(Project project) {
        return testEvent(submitProjectDetailsMessage(null, project), project);
    }

    @Override
    protected ProjectDetailsProcess createNewProcess(Project target, ProjectUser participant) {
        return new ProjectDetailsProcess(participant, target, null);
    }

    public boolean isSubmitted(Project project) {
        ProjectDetailsProcess process = getCurrentProcess(project);
        return process != null && ProjectDetailsState.SUBMITTED.equals(process.getActivityState());
    }

    @Override
    protected ActivityType getActivityType() {
        return PROJECT_SETUP_PROJECT_DETAILS;
    }

    @Override
    protected ProcessRepository<ProjectDetailsProcess> getProcessRepository() {
        return projectDetailsProcessRepository;
    }

    @Override
    protected CrudRepository<Project, Long> getTargetRepository() {
        return projectRepository;
    }

    @Override
    protected CrudRepository<ProjectUser, Long> getParticipantRepository() {
        return projectUserRepository;
    }

    @Override
    protected StateMachine<ProjectDetailsState, ProjectDetailsOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected ProjectDetailsProcess getOrCreateProcess(Message<ProjectDetailsOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<ProjectDetailsOutcomes> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ProjectDetailsOutcomes> mandatoryValueAddedEvent(Project project, ProjectUser projectUser,
                                                                            ProjectDetailsOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }

    private MessageBuilder<ProjectDetailsOutcomes> submitProjectDetailsMessage(ProjectUser projectUser, Project project) {
        return MessageBuilder
                .withPayload(SUBMIT)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }
}
