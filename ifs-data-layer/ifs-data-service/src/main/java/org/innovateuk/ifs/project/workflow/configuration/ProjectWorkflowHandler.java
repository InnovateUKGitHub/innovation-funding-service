package org.innovateuk.ifs.project.workflow.configuration;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

/**
 * {@code ProjectWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the Project's current state, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class ProjectWorkflowHandler extends BaseWorkflowEventHandler<ProjectProcess, ProjectState, ProjectEvent, Project, ProjectUser> {

    @Autowired
    @Qualifier("projectStateMachineFactory")
    private StateMachineFactory<ProjectState, ProjectEvent> stateMachineFactory;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), ProjectState.SETUP);
    }

    public boolean grantOfferLetterApproved(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, ProjectEvent.GOL_APPROVED), project);
    }

    public boolean projectWithdrawn(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, ProjectEvent.PROJECT_WITHDRAWN), project);
    }

    public ProjectState getState(Project project) {
        return getCurrentProcess(project).getActivityState();
    }

    @Override
    protected ProjectProcess createNewProcess(Project target, ProjectUser participant) {
        return new ProjectProcess(participant, target, null);
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.PROJECT_SETUP;
    }

    @Override
    protected ProcessRepository<ProjectProcess> getProcessRepository() {
        return projectProcessRepository;
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
    protected StateMachineFactory<ProjectState, ProjectEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected ProjectProcess getOrCreateProcess(Message<ProjectEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<ProjectEvent> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(ProjectEvent.PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ProjectEvent> mandatoryValueAddedEvent(Project project, ProjectUser projectUser,
                                                                  ProjectEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }

    private MessageBuilder<ProjectEvent> internalUserEvent(Project project, User internalUser, ProjectEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("internalParticipant", internalUser);
    }
}
