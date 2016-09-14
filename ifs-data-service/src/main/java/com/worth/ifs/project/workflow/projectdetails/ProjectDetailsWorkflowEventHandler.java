package com.worth.ifs.project.workflow.projectdetails;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler.PersistStateChangeListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * {@code ProjectDetailsWorkflowEventHandler} is the entry point for triggering the workflow.
 * Based on the Project Detail's current state the next one is tried to transition to by triggering
 * an event.
 */
public class ProjectDetailsWorkflowEventHandler {

    private static final Log LOG = LogFactory.getLog(ProjectDetailsWorkflowEventHandler.class);
    private final PersistStateMachineHandler stateHandler;
    private final PersistStateChangeListener listener = new LocalStateChangeListener();

    public ProjectDetailsWorkflowEventHandler(PersistStateMachineHandler stateHandler) {
        this.stateHandler = stateHandler;
        this.stateHandler.addPersistStateChangeListener(listener);
    }

    public boolean projectCreated(Project project, Long originalLeadApplicantProjectUserId) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.PENDING.getType())
                .setHeader("project", project)
                .setHeader("projectUserId", originalLeadApplicantProjectUserId)
                .build(), ProjectDetailsState.PENDING.name());
    }

    public boolean projectDetailsAllSupplied(ProjectDetailsProcess projectDetails) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.READY_TO_SUBMIT.getType())
                .setHeader("projectDetails", projectDetails)
                .build(), projectDetails.getActivityState().getBackingState().name());
    }

    public boolean submitProjectDetails(Long projectUserId, ProjectDetailsProcess projectDetails) {
        return stateHandler.handleEventWithState(MessageBuilder
                .withPayload(ProjectDetailsOutcomes.SUBMIT.getType())
                .setHeader("projectDetails", projectDetails)
                .setHeader("projectUserId", projectUserId)
                .build(), projectDetails.getActivityState().getBackingState().name());
    }

    private class LocalStateChangeListener implements PersistStateChangeListener {

        @Override
        public void onPersist(State<String, String> state, Message<String> message,
                              Transition<String, String> transition, StateMachine<String, String> stateMachine) {
            if (message != null && message.getHeaders().containsKey("assessment")) {
                LOG.info("STATE: " + state.getId() + " transition: " + transition + " message: " + message + " transition: " + transition + " stateMachine " + stateMachine.getClass().getName());
            }
        }
    }
}
