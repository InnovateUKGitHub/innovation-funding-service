package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.financecheck.domain.FinanceCheckProcess;
import org.innovateuk.ifs.project.financecheck.repository.FinanceCheckProcessRepository;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
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

import static org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes.*;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckState.APPROVED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_FINANCE_CHECKS;

/**
 * {@code FinanceCheckWorkflowService} is the entry point for triggering the workflow.
 * Based on the Finance Check's current state the next one is tried to transition to by triggering
 * an event.
 */
@Component
public class FinanceCheckWorkflowHandler extends BaseWorkflowEventHandler<FinanceCheckProcess, FinanceCheckState, FinanceCheckOutcomes, PartnerOrganisation, ProjectUser> {

    @Autowired
    @Qualifier("financeCheckStateMachine")
    private StateMachine<FinanceCheckState, FinanceCheckOutcomes> stateMachine;

    @Autowired
    private FinanceCheckProcessRepository financeCheckProcessRepository;

    @Autowired
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public boolean projectCreated(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return fireEvent(projectCreatedEvent(partnerOrganisation, originalLeadApplicantProjectUser), FinanceCheckState.PENDING);
    }

    public boolean approveFinanceCheck(PartnerOrganisation partnerOrganisation, User financeTeamUser) {
        return fireEvent(approveFinanceCheckMessage(financeTeamUser, partnerOrganisation), partnerOrganisation);
    }

    @Override
    protected FinanceCheckProcess createNewProcess(PartnerOrganisation target, ProjectUser participant) {
        return new FinanceCheckProcess(participant, target, null);
    }

    public boolean isApproved(PartnerOrganisation partnerOrganisation) {
        FinanceCheckProcess process = getCurrentProcess(partnerOrganisation);
        return process != null && APPROVED.equals(process.getActivityState());
    }

    @Override
    protected ActivityType getActivityType() {
        return PROJECT_SETUP_FINANCE_CHECKS;
    }

    @Override
    protected ProcessRepository<FinanceCheckProcess> getProcessRepository() {
        return financeCheckProcessRepository;
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
    protected StateMachine<FinanceCheckState, FinanceCheckOutcomes> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected FinanceCheckProcess getOrCreateProcess(Message<FinanceCheckOutcomes> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    private MessageBuilder<FinanceCheckOutcomes> projectCreatedEvent(PartnerOrganisation partnerOrganisation, ProjectUser originalLeadApplicantProjectUser) {
        return MessageBuilder
                .withPayload(PROJECT_CREATED)
                .setHeader("target", partnerOrganisation)
                .setHeader("participant", originalLeadApplicantProjectUser);
    }

    private MessageBuilder<FinanceCheckOutcomes> approveFinanceCheckMessage(User financeTeamMember, PartnerOrganisation partnerOrganisation) {
        return MessageBuilder
                .withPayload(APPROVE)
                .setHeader("target", partnerOrganisation)
                .setHeader("internalParticipant", financeTeamMember);
    }
}
