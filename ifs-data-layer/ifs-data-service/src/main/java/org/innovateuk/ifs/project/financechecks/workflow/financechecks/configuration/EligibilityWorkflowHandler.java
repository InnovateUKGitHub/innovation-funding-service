package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.repository.PartnerOrganisationRepository;
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
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;


import static org.innovateuk.ifs.project.finance.resource.EligibilityEvent.NOT_REQUESTING_FUNDING;
import static org.innovateuk.ifs.project.finance.resource.EligibilityEvent.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.EligibilityEvent.ELIGIBILITY_APPROVED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_ELIGIBILITY;


/**
 * {@code EligibilityWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the current state of Eligibility, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class EligibilityWorkflowHandler extends BaseWorkflowEventHandler<EligibilityProcess, EligibilityState, EligibilityEvent, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("eligibilityStateMachine")
    private StateMachine<EligibilityState, EligibilityEvent> stateMachine;

    @Autowired
    private EligibilityProcessRepository eligibilityProcessRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(partnerOrganisation, originalLeadApplicantProjectUser), EligibilityState.REVIEW);
    }

    public boolean notRequestingFunding(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, NOT_REQUESTING_FUNDING), partnerOrganisation);
    }

    public boolean eligibilityApproved(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, ELIGIBILITY_APPROVED), partnerOrganisation);
    }

    public EligibilityProcess getProcess(PartnerOrganisation partnerOrganisation) {
        return getCurrentProcess(partnerOrganisation);
    }

    public EligibilityState getState(PartnerOrganisation partnerOrganisation) {
        EligibilityProcess process = getCurrentProcess(partnerOrganisation);
        return process != null ? process.getActivityState() : EligibilityState.REVIEW;
    }

    @Override
    protected EligibilityProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new EligibilityProcess(participant, target, null);
    }

    @Override
    protected ActivityType getActivityType() {
        return PROJECT_SETUP_ELIGIBILITY;
    }

    @Override
    protected ProcessRepository<EligibilityProcess> getProcessRepository() {
        return eligibilityProcessRepository;
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
    protected StateMachine<EligibilityState, EligibilityEvent> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected EligibilityProcess getOrCreateProcess(Message<EligibilityEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<EligibilityEvent> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<EligibilityEvent> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                               EligibilityEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}

