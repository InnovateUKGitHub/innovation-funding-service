package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.finance.resource.ViabilityEvent;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.ViabilityProcessRepository;
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

import static org.innovateuk.ifs.project.finance.resource.ViabilityEvent.*;

/**
 * {@code ViabilityWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the current state of Viability, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class ViabilityWorkflowHandler extends BaseWorkflowEventHandler<ViabilityProcess, ViabilityState, ViabilityEvent, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("viabilityStateMachineFactory")
    private StateMachineFactory<ViabilityState, ViabilityEvent> stateMachineFactory;

    @Autowired
    private ViabilityProcessRepository viabilityProcessRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(partnerOrganisation, originalLeadApplicantProjectUser), ViabilityState.REVIEW);
    }

    public boolean viabilityApproved(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, VIABILITY_APPROVED), partnerOrganisation);
    }

    public boolean viabilityNotApplicable(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, VIABILITY_NOT_APPLICABLE), partnerOrganisation);
    }

    public boolean viabilityReset(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, VIABILITY_RESET), partnerOrganisation);
    }

    public ViabilityProcess getProcess(PartnerOrganisation partnerOrganisation) {
        return getCurrentProcess(partnerOrganisation);
    }

    public ViabilityState getState(PartnerOrganisation partnerOrganisation) {
        ViabilityProcess process = getCurrentProcess(partnerOrganisation);
        return process != null ? process.getProcessState() : ViabilityState.REVIEW;
    }

    @Override
    protected ViabilityProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new ViabilityProcess(participant, target, null);
    }

    @Override
    protected ProcessRepository<ViabilityProcess> getProcessRepository() {
        return viabilityProcessRepository;
    }

    @Override
    protected CrudRepository<PartnerOrganisation, Long> getTargetRepository() {
        return partnerOrganisationRepository;
    }

    @Override
    protected CrudRepository<ProjectUser, Long> getParticipantRepository() {
        return projectUserRepository;
    }

    @Override
    protected StateMachineFactory<ViabilityState, ViabilityEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected ViabilityProcess getOrCreateProcess(Message<ViabilityEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<ViabilityEvent> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ViabilityEvent> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                             ViabilityEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}