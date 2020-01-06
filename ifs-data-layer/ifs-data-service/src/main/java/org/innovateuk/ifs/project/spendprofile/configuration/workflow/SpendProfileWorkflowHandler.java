package org.innovateuk.ifs.project.spendprofile.configuration.workflow;

import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent.*;

@Component
public class SpendProfileWorkflowHandler extends BaseWorkflowEventHandler<SpendProfileProcess, SpendProfileState, SpendProfileEvent, Project, ProjectUser> {

    @Autowired
    @Qualifier("spendProfileStateMachineFactory")
    private StateMachineFactory<SpendProfileState, SpendProfileEvent> stateMachineFactory;

    @Autowired
    private SpendProfileProcessRepository spendProfileProcessRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    public boolean projectCreated(Project project, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(project, originalLeadApplicantProjectUser), SpendProfileState.PENDING);
    }

    public boolean spendProfileGenerated(Project project, User internalUser) {
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

    public boolean isAlreadyGenerated(Project project) {
        SpendProfileProcess process = getCurrentProcess(project);
        if (process == null)
            return false;
        return !SpendProfileState.PENDING.equals(process.getProcessState());
    }

    public boolean projectHasNoPendingPartners(Project project) {
        boolean pendingPartnerProgressComplete = project.getPartnerOrganisations()
                .stream()
                .noneMatch(PartnerOrganisation::isPendingPartner);

        boolean noSentStatusPartnerInvites = projectPartnerInviteRepository.existsByProjectIdAndStatus(project.getId(), SENT);

        return pendingPartnerProgressComplete && !noSentStatusPartnerInvites;
    }

    public boolean isReadyToApprove(Project project) {
        SpendProfileProcess process = getCurrentProcess(project);
        return process != null && SpendProfileState.SUBMITTED.equals(process.getProcessState());
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
        if (process != null && SpendProfileState.APPROVED.equals(process.getProcessState())) {
            return ApprovalType.APPROVED;
        } else if (process != null && SpendProfileState.REJECTED.equals(process.getProcessState())) {
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
    protected StateMachineFactory<SpendProfileState, SpendProfileEvent> getStateMachineFactory() {
        return stateMachineFactory;
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