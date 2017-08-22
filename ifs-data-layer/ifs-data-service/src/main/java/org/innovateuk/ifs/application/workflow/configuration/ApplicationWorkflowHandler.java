package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION;

/**
 * Workflow handler for firing {@link ApplicationEvent} events.
 */
@Component
public class ApplicationWorkflowHandler extends BaseWorkflowEventHandler<ApplicationProcess, ApplicationState, ApplicationEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("applicationProcessStateMachine")
    private StateMachine<ApplicationState, ApplicationEvent> stateMachine;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Override
    protected ApplicationProcess createNewProcess(Application target, ProcessRole participant) {
        return new ApplicationProcess(target, participant, new ActivityState(APPLICATION, State.CREATED));
    }

    @Override
    protected ActivityType getActivityType() {
        return APPLICATION;
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
    protected StateMachine<ApplicationState, ApplicationEvent> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected ApplicationProcess getOrCreateProcess(Message<ApplicationEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    public boolean open(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.OPENED), application);
    }

    public boolean submit(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.SUBMITTED), application);
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
        return fireEvent(applicationMessage(application, ApplicationEvent.APPROVED), application);
    }

    public boolean reject(Application application) {
        return fireEvent(applicationMessage(application, ApplicationEvent.REJECTED), application);
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
            case OPEN:
                return open(application);
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

    private boolean applicationStateMatches(Application application, ApplicationState... applicationStates) {
        ApplicationProcess applicationProcess = application.getApplicationProcess();
        return Stream.of(applicationStates).anyMatch(applicationProcess::isInState);
    }
}
