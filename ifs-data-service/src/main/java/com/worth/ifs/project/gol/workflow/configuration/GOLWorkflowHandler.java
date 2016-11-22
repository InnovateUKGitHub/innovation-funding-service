package com.worth.ifs.project.gol.workflow.configuration;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.gol.domain.GOLProcess;
import com.worth.ifs.project.gol.repository.GrantOfferLetterProcessRepository;
import com.worth.ifs.project.gol.resource.GOLOutcomes;
import com.worth.ifs.project.gol.resource.GOLState;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
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

import static com.worth.ifs.project.gol.resource.GOLOutcomes.PROJECT_CREATED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_SENT;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_SIGNED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_APPROVED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_REJECTED;
import static com.worth.ifs.workflow.domain.ActivityType.PROJECT_SETUP_GRANT_OFFER_LETTER;

/**
 * {@code GOLWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the GOL's current state, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class GOLWorkflowHandler extends BaseWorkflowEventHandler<GOLProcess, GOLState, GOLOutcomes, Project, ProjectUser> {

    @Autowired
    @Qualifier("golStateMachine")
    private StateMachine<GOLState, GOLOutcomes> stateMachine;

    @Autowired
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), GOLState.PENDING);
    }

    public boolean grantOfferLetterSent(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, GOL_SENT), project);
    }

    public boolean grantOfferLetterSigned(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, GOL_SIGNED), project);
    }

    public boolean grantOfferLetterRejected(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, GOL_REJECTED), project);
    }

    public boolean grantOfferLetterApproved(Project project, ProjectUser projectUser) {
        return fireEvent(mandatoryValueAddedEvent(project, projectUser, GOL_APPROVED), project);
    }

    @Override
    protected GOLProcess createNewProcess(Project target, ProjectUser participant) {
        return new GOLProcess(participant, target, null);
    }

    @Override
    protected ActivityType getActivityType() {
        return PROJECT_SETUP_GRANT_OFFER_LETTER;
    }

    @Override
    protected ProcessRepository<GOLProcess> getProcessRepository() {
        return grantOfferLetterProcessRepository;
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
    protected StateMachine<GOLState, GOLOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected GOLProcess getOrCreateProcess(Message<GOLOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<GOLOutcomes> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<GOLOutcomes> mandatoryValueAddedEvent(Project project, ProjectUser projectUser,
                                                                            GOLOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }
}
