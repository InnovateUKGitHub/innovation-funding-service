package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financecheck.repository.EligibilityProcessRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes;
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


import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.NOT_REQUESTING_FUNDING;
import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.ELIGIBILITY_APPROVED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_ELIGIBILITY;


/**
 * {@code EligibilityWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the current state of Eligibility, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class EligibilityWorkflowHandler extends BaseWorkflowEventHandler<EligibilityProcess, EligibilityState, EligibilityOutcomes, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("eligibilityStateMachine")
    private StateMachine<EligibilityState, EligibilityOutcomes> stateMachine;

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
    protected StateMachine<EligibilityState, EligibilityOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected EligibilityProcess getOrCreateProcess(Message<EligibilityOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<EligibilityOutcomes> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<EligibilityOutcomes> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                                  EligibilityOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}

