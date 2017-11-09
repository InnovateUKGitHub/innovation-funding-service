package org.innovateuk.ifs.project.spendprofile.configuration.workflow;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
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

import java.util.function.BiFunction;

import static org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent.*;

@Component
public class SpendProfileWorkflowHandler extends BaseWorkflowEventHandler<SpendProfileProcess, SpendProfileState, SpendProfileEvent, Project, ProjectUser> {
    @Autowired
    @Qualifier("spendProfileStateMachine")
    private StateMachine<SpendProfileState, SpendProfileEvent> stateMachine;

    @Autowired
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), SpendProfileState.PENDING);
    }

    public boolean sendProfileGenerated(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, SPEND_PROFILE_GENERATED), project);
    }

    public boolean spendProfileSubmitted(Project project, ProjectUser projectUser) {
        return fireEvent(externalUserEvent(project, projectUser, SPEND_PROFILE_SUBMITTED), project);
    }

    public boolean spendProfileRejected(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, SPEND_PROFILE_REJECTED), project);
    }

    public boolean spendProfileApproved(Project project, User internalUser) {
        return fireEvent(internalUserEvent(project, internalUser, SPEND_PROFILE_APPROVED), project);
    }

    public boolean submit(Project project) {
        return getIfProjectAndUserValid(project, this::spendProfileSubmitted);
    }

    public boolean isReadyToGenerate(Project project) {
        SpendProfileProcess process = getCurrentProcess(project);
        return process != null && SpendProfileState.PENDING.equals(process.getActivityState());
    }

    public boolean isReadyToApprove(Project project) {
        SpendProfileProcess process = getCurrentProcess(project);
        return process != null && SpendProfileState.SUBMITTED.equals(process.getActivityState());
    }

    private boolean getIfProjectAndUserValid(Project project, BiFunction<Project, ProjectUser, Boolean> fn) {
        SpendProfileProcess process = getCurrentProcess(project);
        if(process == null)
            return false;
        ProjectUser projectUser = process.getParticipant();
        if(projectUser == null)
            return false;
        return fn.apply(project, projectUser);
    }

    public ApprovalType getApproval(Project project) {
        SpendProfileProcess process = getCurrentProcess(project);
        if (process != null && SPEND_PROFILE_APPROVED.equals(process.getActivityState())) {
            return ApprovalType.APPROVED;
        } else if (process != null && SPEND_PROFILE_REJECTED.equals(process.getActivityState())) {
            return ApprovalType.REJECTED;
        } else {
            return ApprovalType.UNSET;
        }
    }

    @Override
    protected SpendProfileProcess createNewProcess(Project target, ProjectUser participant) {
        return new SpendProfileProcess(participant, target, null);
    }

    @Override
    protected ActivityType getActivityType() {
        return ActivityType.PROJECT_SETUP_SPEND_PROFILE;
    }

    @Override
    protected ProcessRepository<SpendProfileProcess> getProcessRepository() {
        return spendProfileProcessRepository;
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
    protected StateMachine<SpendProfileState, SpendProfileEvent> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected SpendProfileProcess getOrCreateProcess(Message<SpendProfileEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<SpendProfileEvent> projectCreatedEvent(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(SpendProfileEvent.PROJECT_CREATED)
                .setHeader("target", project)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<SpendProfileEvent> externalUserEvent(Project project,
                                                                ProjectUser projectUser,
                                                                SpendProfileEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("participant", projectUser);
    }

    private MessageBuilder<SpendProfileEvent> internalUserEvent(Project project,
                                                                User internalUser,
                                                                SpendProfileEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", project)
                .setHeader("internalParticipant", internalUser);
    }
}
