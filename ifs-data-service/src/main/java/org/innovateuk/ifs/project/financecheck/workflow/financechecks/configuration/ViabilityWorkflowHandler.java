package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financecheck.repository.ViabilityProcessRepository;
import org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
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

import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.ORGANISATION_IS_ACADEMIC;
import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.VIABILITY_APPROVED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_VIABILITY;

/**
 * {@code ViabilityWorkflowService} is the entry point for triggering the workflow.
 *
 * Based on the current state of Viability, the next one is tried to transition to by triggering an event.
 *
 */
@Component
public class ViabilityWorkflowHandler extends BaseWorkflowEventHandler<ViabilityProcess, ViabilityState, ViabilityOutcomes, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("viabilityStateMachine")
    private StateMachine<ViabilityState, ViabilityOutcomes> stateMachine;

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

    public boolean organisationIsAcademic(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, ORGANISATION_IS_ACADEMIC), partnerOrganisation);
    }

    public ViabilityProcess getProcess(PartnerOrganisation partnerOrganisation) {
        return getCurrentProcess(partnerOrganisation);
    }

    public ViabilityState getState(PartnerOrganisation partnerOrganisation) {
        ViabilityProcess process = getCurrentProcess(partnerOrganisation);
        return process != null ? process.getActivityState() : ViabilityState.REVIEW;
    }

    @Override
    protected ViabilityProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new ViabilityProcess(participant, target, null);
    }

    @Override
    protected ActivityType getActivityType() {
        return PROJECT_SETUP_VIABILITY;
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
    protected StateMachine<ViabilityState, ViabilityOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected ViabilityProcess getOrCreateProcess(Message<ViabilityOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<ViabilityOutcomes> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<ViabilityOutcomes> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                          ViabilityOutcomes event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}

