package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.finance.resource.FundingRulesEvent;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;
import org.innovateuk.ifs.project.financechecks.domain.FundingRulesProcess;
import org.innovateuk.ifs.project.financechecks.repository.FundingRulesProcessRepository;
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

import static org.innovateuk.ifs.project.finance.resource.FundingRulesEvent.PROJECT_CREATED;

@Component
public class FundingRulesWorkflowHandler  extends BaseWorkflowEventHandler<FundingRulesProcess, FundingRulesState, FundingRulesEvent, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("fundingRulesStateMachineFactory")
    private StateMachineFactory<FundingRulesState, FundingRulesEvent> stateMachineFactory;

    @Autowired
    private FundingRulesProcessRepository fundingRulesProcessRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(partnerOrganisation, originalLeadApplicantProjectUser), FundingRulesState.REVIEW);
    }

    public boolean fundingRulesApproved(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, FundingRulesEvent.FUNDING_RULES_APPROVED), partnerOrganisation);
    }

    public boolean fundingRulesUpdated(PartnerOrganisation partnerOrganisation, User internalUser) {
        return fireEvent(internalUserEvent(partnerOrganisation, internalUser, FundingRulesEvent.FUNDING_RULES_UPDATED), partnerOrganisation);
    }

    public FundingRulesProcess getProcess(PartnerOrganisation partnerOrganisation) {
        return getCurrentProcess(partnerOrganisation);
    }

    public FundingRulesState getState(PartnerOrganisation partnerOrganisation) {
        FundingRulesProcess process = getCurrentProcess(partnerOrganisation);
        return process != null ? process.getProcessState() : FundingRulesState.REVIEW;
    }

    @Override
    protected FundingRulesProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new FundingRulesProcess(participant, target, null);
    }

    @Override
    protected ProcessRepository<FundingRulesProcess> getProcessRepository() {
        return fundingRulesProcessRepository;
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
    protected StateMachineFactory<FundingRulesState, FundingRulesEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected FundingRulesProcess getOrCreateProcess(Message<FundingRulesEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<FundingRulesEvent> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<FundingRulesEvent> internalUserEvent(PartnerOrganisation partnerOrganisation, User internalUser,
                                                               FundingRulesEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", internalUser);
    }
}