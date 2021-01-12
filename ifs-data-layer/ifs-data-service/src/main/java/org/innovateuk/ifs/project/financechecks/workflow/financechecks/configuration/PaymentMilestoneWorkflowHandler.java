package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess;
import org.innovateuk.ifs.project.financechecks.repository.PaymentMilestoneProcessRepository;
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

/**
 * Based on the current state of PaymentMilestone, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class PaymentMilestoneWorkflowHandler extends BaseWorkflowEventHandler<PaymentMilestoneProcess, PaymentMilestoneState, PaymentMilestoneEvent, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("paymentMilestoneStateMachineFactory")
    private StateMachineFactory<PaymentMilestoneState, PaymentMilestoneEvent> stateMachineFactory;

    @Autowired
    private PaymentMilestoneProcessRepository paymentMilestoneProcessRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(partnerOrganisation, originalLeadApplicantProjectUser), PaymentMilestoneState.REVIEW);
    }

    public boolean paymentMilestoneApproved(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, PaymentMilestoneEvent.PAYMENT_MILESTONE_APPROVED), partnerOrganisation);
    }

    public boolean paymentMilestoneReset(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, PaymentMilestoneEvent.PAYMENT_MILESTONE_RESET), partnerOrganisation);
    }

    public PaymentMilestoneProcess getProcess(PartnerOrganisation partnerOrganisation) {
        return getCurrentProcess(partnerOrganisation);
    }

    public PaymentMilestoneState getState(PartnerOrganisation partnerOrganisation) {
        PaymentMilestoneProcess process = getCurrentProcess(partnerOrganisation);
        return process != null ? process.getProcessState() : PaymentMilestoneState.REVIEW;
    }

    @Override
    protected PaymentMilestoneProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new PaymentMilestoneProcess(participant, target, null);
    }

    @Override
    protected ProcessRepository<PaymentMilestoneProcess> getProcessRepository() {
        return paymentMilestoneProcessRepository;
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
    protected StateMachineFactory<PaymentMilestoneState, PaymentMilestoneEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected PaymentMilestoneProcess getOrCreateProcess(Message<PaymentMilestoneEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<PaymentMilestoneEvent> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PaymentMilestoneEvent.PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<PaymentMilestoneEvent> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                                    PaymentMilestoneEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}