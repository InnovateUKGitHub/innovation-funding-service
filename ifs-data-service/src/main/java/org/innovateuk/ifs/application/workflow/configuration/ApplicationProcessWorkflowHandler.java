package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.repository.ApplicationProcessRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationOutcome;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION;

@Component
public class ApplicationProcessWorkflowHandler extends BaseWorkflowEventHandler<ApplicationProcess, ApplicationState, ApplicationOutcome, Application, ProcessRole> {

    @Autowired
    @Qualifier("applicationProcessStateMachine")
    private StateMachine<ApplicationState, ApplicationOutcome> stateMachine;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Override
    protected ApplicationProcess createNewProcess(Application target, ProcessRole participant) {
        return new ApplicationProcess(target, participant);
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
    protected StateMachine<ApplicationState, ApplicationOutcome> getStateMachine() {
        return stateMachine;
    }

    @Override
    protected ApplicationProcess getOrCreateProcess(Message<ApplicationOutcome> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    public boolean open(Application application) {
        return fireEvent(applicationMessage(application, ApplicationOutcome.OPENED), application);
    }

    public boolean submit(Application application) {
        return fireEvent(applicationMessage(application, ApplicationOutcome.SUBMITTED), application);
    }

    public boolean approve(Application application) {
        return fireEvent(applicationMessage(application, ApplicationOutcome.APPROVED), application);
    }

    public boolean reject(Application application) {
        return fireEvent(applicationMessage(application, ApplicationOutcome.REJECTED), application);
    }

    public boolean notifyFromApplicationStatus(Application application, ApplicationStatus applicationStatus) {
        switch (applicationStatus) {
            case CREATED:
                return false; // TODO might need to allow a move to created, even though it shouldn't be possible
            case SUBMITTED:
                return submit(application);
            case APPROVED:
                return approve(application);
            case REJECTED:
                return reject(application);
            case OPEN:
                return open(application);
            default:
                throw new IllegalArgumentException("unknown applicationStatus: " + applicationStatus);
        }
    }

    private MessageBuilder<ApplicationOutcome> applicationMessage(Application application, ApplicationOutcome event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", application);
    }
}
