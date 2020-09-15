package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;

/**
 * Workflow handler for firing {@link ApplicationEvent} events.
 */
@Component
public class ApplicationWorkflowHandler extends BaseWorkflowEventHandler<ApplicationProcess, ApplicationState, ApplicationEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("applicationProcessStateMachineFactory")
    private StateMachineFactory<ApplicationState, ApplicationEvent> stateMachineFactory;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Override
    protected ApplicationProcess createNewProcess(Application target, ProcessRole participant) {
        return new ApplicationProcess(target, participant, ApplicationState.CREATED);
    }

    @Override
    protected ApplicationProcessRepository getProcessRepository() {
        return applicationProcessRepository;
    }

    @Override
    protected ApplicationRepository getTargetRepository() {
        return applicationRepository;
    }

    @Override
    protected ProcessRoleRepository getParticipantRepository() {
        return processRoleRepository;
    }

    @Override
    protected StateMachineFactory<ApplicationState, ApplicationEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected ApplicationProcess getOrCreateProcess(Message<ApplicationEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    public boolean open(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.OPEN), application);
    }

    public boolean submit(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.SUBMIT), application);
    }

    public boolean reopen(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.REOPEN), application);
    }

    public boolean markIneligible(Application application, IneligibleOutcome ineligibleOutcome) {
        return fireEvent(markIneligibleMessage(application, ineligibleOutcome), application);
    }

    public boolean informIneligible(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.INFORM_INELIGIBLE), application);
    }

    public boolean reinstateIneligible(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.REINSTATE_INELIGIBLE), application);
    }

    public boolean approve(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.APPROVE), application);
    }

    public boolean reject(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.REJECT), application);
    }

    public boolean notifyFromApplicationState(Application application, ApplicationState applicationState) {
        switch (applicationState) {
            case CREATED:
                return false;
            case SUBMITTED:
                boolean reinstating = applicationStateMatches(application, INELIGIBLE, INELIGIBLE_INFORMED);
                if (reinstating) {
                    return reinstateIneligible(application);
                }
                return submit(application);
            case APPROVED:
                return approve(application);
            case REJECTED:
                return reject(application);
            case OPENED:
                if (application.isSubmitted()) {
                    return reopen(application);
                } else {
                    return open(application);
                }
            default:
                throw new IllegalArgumentException("unknown applicationState: " + applicationState);
        }
    }

    private static MessageBuilder<ApplicationEvent> markIneligibleMessage(Application application, IneligibleOutcome ineligibleOutcome) {
        return applicationMessage(application, ApplicationEvent.MARK_INELIGIBLE)
                .setHeader("ineligible", ineligibleOutcome);
    }

    private static MessageBuilder<ApplicationEvent> applicationMessage(Application application, ApplicationEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", application)
                .setHeader("applicationProcess", application.getApplicationProcess());
    }

    private static MessageBuilder<ApplicationEvent> applicationMessageWithInternalUser(Application application, ApplicationEvent event, User internalUser) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", application)
                .setHeader("applicationProcess", application.getApplicationProcess())
                .setHeader("internalParticipant", internalUser);
    }

    private boolean applicationStateMatches(Application application, ApplicationState... applicationStates) {
        ApplicationProcess applicationProcess = application.getApplicationProcess();
        return Stream.of(applicationStates).anyMatch(applicationProcess::isInState);
    }
}