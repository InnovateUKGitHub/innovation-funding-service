package org.innovateuk.ifs.project.grantofferletter.configuration.workflow;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
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

import java.util.function.BiFunction;

import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_GRANT_OFFER_LETTER;

/**
 * {@code GOLWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the GOL's current state, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class GrantOfferLetterWorkflowHandler extends BaseWorkflowEventHandler<GOLProcess, GrantOfferLetterState, GrantOfferLetterEvent, Project, ProjectUser> {

    @Autowired
    @Qualifier("golStateMachineFactory")
    private StateMachineFactory<GrantOfferLetterState, GrantOfferLetterEvent> stateMachineFactory;

    @Autowired
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), GrantOfferLetterState.PENDING);
    }

    public boolean grantOfferLetterSent(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, GOL_SENT), project);
    }

    public boolean grantOfferLetterSigned(Project project, ProjectUser projectUser) {
        return fireEvent(externalUserEvent(project, projectUser, GOL_SIGNED), project);
    }

    public boolean grantOfferLetterRejected(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, SIGNED_GOL_REJECTED), project);
    }

    public boolean grantOfferLetterApproved(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, SIGNED_GOL_APPROVED), project);
    }

    public boolean isSendAllowed(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && GrantOfferLetterState.PENDING.equals(process.getProcessState());
    }

    public boolean removeGrantOfferLetter(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, GOL_REMOVED), project);
    }

    public boolean removeSignedGrantOfferLetter(Project project, User user) {

        ProjectUser projectManager = projectUserRepository.findByProjectIdAndRoleAndUserId(project.getId(),
                ProjectParticipantRole.PROJECT_MANAGER, user.getId());

        if (projectManager == null) {
            return false;
        }

        return fireEvent(externalUserEvent(project, projectManager, SIGNED_GOL_REMOVED), project);
    }

    @ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with usage of getGrantOfferLetterState()")
    public boolean isAlreadySent(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && !GrantOfferLetterState.PENDING.equals(process.getProcessState());
    }

    public boolean isApproved(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && GrantOfferLetterState.APPROVED.equals(process.getProcessState());
    }

    public boolean isRejected(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && GrantOfferLetterState.SENT.equals(process.getProcessState()) &&
                SIGNED_GOL_REJECTED.getType().equalsIgnoreCase(process.getProcessEvent());
    }

    public boolean isReadyToApprove(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && GrantOfferLetterState.READY_TO_APPROVE.equals(process.getProcessState());
    }

    public boolean isSent(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null && GrantOfferLetterState.SENT.equals(process.getProcessState());
    }

    public GrantOfferLetterState getState(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null? process.getProcessState() : GrantOfferLetterState.PENDING;
    }

    private boolean getIfProjectAndUserValid(Project project, BiFunction<Project, ProjectUser, Boolean> fn) {
        GOLProcess process = getCurrentProcess(project);
        if(process == null)
            return false;
        ProjectUser projectUser = process.getParticipant();
        if(projectUser == null)
            return false;
        return fn.apply(project, projectUser);
    }

    public GrantOfferLetterEvent getLastProcessEvent(Project project) {
        GOLProcess process = getCurrentProcess(project);
        return process != null ? GrantOfferLetterEvent.getByType(process.getProcessEvent()) : null;
    }

    public boolean sign(Project project) {
        return getIfProjectAndUserValid(project, this::grantOfferLetterSigned);
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
    protected StateMachineFactory<GrantOfferLetterState, GrantOfferLetterEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected GOLProcess getOrCreateProcess(Message<GrantOfferLetterEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<GrantOfferLetterEvent> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<GrantOfferLetterEvent> externalUserEvent(Project project, ProjectUser projectUser,
                                                                    GrantOfferLetterEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }

    private MessageBuilder<GrantOfferLetterEvent> internalUserEvent(Project project, User internalUser,
                                                                    GrantOfferLetterEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("internalParticipant", internalUser);
    }
}
