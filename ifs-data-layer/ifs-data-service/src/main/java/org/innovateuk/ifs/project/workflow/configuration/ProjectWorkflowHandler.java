package org.innovateuk.ifs.project.workflow.configuration;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ProjectOutcomes;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

/**
 * {@code ProjectWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the Project's current state, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class ProjectWorkflowHandler extends BaseWorkflowEventHandler<ProjectProcess, ProjectState, ProjectOutcomes, Project, ProjectUser> {

    @Autowired
    @Qualifier("projectStateMachine")
    private StateMachine<ProjectState, ProjectOutcomes> stateMachine;

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
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, ProjectOutcomes.GOL_APPROVED), project);
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
    protected StateMachine<ProjectState, ProjectOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected ProjectProcess getOrCreateProcess(Message<ProjectOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<ProjectOutcomes> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(ProjectOutcomes.PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ProjectOutcomes> mandatoryValueAddedEvent(Project project, ProjectUser projectUser,
                                                                     ProjectOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }
}
