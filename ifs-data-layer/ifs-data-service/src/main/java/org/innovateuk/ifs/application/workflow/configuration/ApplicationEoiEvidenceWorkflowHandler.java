package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceProcessRepository;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceResponseRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceEvent;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
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

@Component
public class ApplicationEoiEvidenceWorkflowHandler extends BaseWorkflowEventHandler<ApplicationEoiEvidenceProcess, ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent, ApplicationEoiEvidenceResponse, ProcessRole> {
    @Autowired
    @Qualifier("applicationEoiEvidenceProcessStateMachineFactory")
    private StateMachineFactory<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent> stateMachineFactory;

    @Autowired
    private ApplicationEoiEvidenceResponseRepository applicationEoiEvidenceResponseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ApplicationEoiEvidenceProcessRepository applicationEoiEvidenceProcessRepository;

    @Override
    protected ApplicationEoiEvidenceProcess createNewProcess(ApplicationEoiEvidenceResponse target, ProcessRole participant) {
        return new ApplicationEoiEvidenceProcess(participant, target, ApplicationEoiEvidenceState.NOT_SUBMITTED);
    }

    @Override
    protected ApplicationEoiEvidenceProcessRepository getProcessRepository() {
        return applicationEoiEvidenceProcessRepository;
    }

    @Override
    protected ApplicationEoiEvidenceResponseRepository getTargetRepository() {
        return applicationEoiEvidenceResponseRepository;
    }

    @Override
    protected ProcessRoleRepository getParticipantRepository() {
        return processRoleRepository;
    }

    @Override
    protected StateMachineFactory<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected ApplicationEoiEvidenceProcess getOrCreateProcess(Message<ApplicationEoiEvidenceEvent> message) {
        return getOrCreateProcessCommonStrategy(message);
    }

    public boolean submit(ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse, ProcessRole participant, User internalUser) {
        return fireEvent(submitEvent(applicationEoiEvidenceResponse, participant, internalUser), applicationEoiEvidenceResponse);
    }

    public boolean documentUploaded(ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse) {
        return fireEvent(unSubmitEvent(applicationEoiEvidenceResponse), ApplicationEoiEvidenceState.NOT_SUBMITTED);
    }

    private MessageBuilder<ApplicationEoiEvidenceEvent> submitEvent(ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse, ProcessRole  participant, User internalUser) {
        return MessageBuilder
                .withPayload(ApplicationEoiEvidenceEvent.SUBMIT)
                .setHeader("target", applicationEoiEvidenceResponse)
                .setHeader("participant", participant)
                .setHeader("internalParticipant", internalUser)
                .setHeader("applicationEoiEvidenceProcess", applicationEoiEvidenceResponse.getApplicationEoiEvidenceProcess());
    }

    private MessageBuilder<ApplicationEoiEvidenceEvent> unSubmitEvent(ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse) {
        return MessageBuilder
                .withPayload(ApplicationEoiEvidenceEvent.UNSUBMIT)
                .setHeader("target", applicationEoiEvidenceResponse)
                .setHeader("applicationEoiEvidenceProcess", applicationEoiEvidenceResponse.getApplicationEoiEvidenceProcess());
    }
}
