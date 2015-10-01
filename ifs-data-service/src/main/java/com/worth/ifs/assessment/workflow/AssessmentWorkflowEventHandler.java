package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentEvents;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler.PersistStateChangeListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

public class AssessmentWorkflowEventHandler {
    private final Log log = LogFactory.getLog(getClass());
    private final PersistStateMachineHandler stateHandler;
    private final PersistStateChangeListener listener = new LocalStateChangeListener();

    public AssessmentWorkflowEventHandler(PersistStateMachineHandler stateHandler) {
        this.stateHandler = stateHandler;
        this.stateHandler.addPersistStateChangeListener(listener);
    }

    public void acceptInvitation(Long applicationId, Long assessorId, Assessment assessment) {
        stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentEvents.ACCEPT.getEvent())
                .setHeader("assessment", assessment)
                .setHeader("applicationId", applicationId)
                .setHeader("assessorId", assessorId)
                .build(), assessment.getProcessStatus());
    }

    public void rejectInvitation(Long applicationId, Long assessorId, Assessment assessment) {
        stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentEvents.REJECT.getEvent())
                .setHeader("assessment", assessment)
                .setHeader("applicationId", applicationId)
                .setHeader("assessorId", assessorId)
                .build(), assessment.getProcessStatus());
    }

    public void recommend(Long applicationId, Long assessorId, Assessment assessment) {
        stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentEvents.RECOMMEND.getEvent())
                .setHeader("assessment", assessment)
                .setHeader("applicationId", applicationId)
                .setHeader("assessorId", assessorId)
                .build(), assessment.getProcessStatus());
    }

    public void submit(Assessment assessment) {
        stateHandler.handleEventWithState(MessageBuilder
                .withPayload(AssessmentEvents.SUBMIT.getEvent())
                .setHeader("assessment", assessment)
                .build(), assessment.getProcessStatus());
    }

    private class LocalStateChangeListener implements PersistStateChangeListener {

        @Override
        public void onPersist(State<String, String> state, Message<String> message,
                              Transition<String, String> transition, StateMachine<String, String> stateMachine) {
            if (message != null && message.getHeaders().containsKey("assessment")) {
                log.info("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
            }
        }
    }
}
