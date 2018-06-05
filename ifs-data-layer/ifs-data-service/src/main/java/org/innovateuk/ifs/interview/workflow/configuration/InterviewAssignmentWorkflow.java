package org.innovateuk.ifs.interview.workflow.configuration;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.actions.FeedbackResponseInterviewAssignmentAction;
import org.innovateuk.ifs.interview.workflow.actions.NotifyInterviewAssignmentAction;
import org.innovateuk.ifs.interview.workflow.actions.WithdrawResponseInterviewAssignmentAction;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent.NOTIFY;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent.RESPOND;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentEvent.WITHDRAW_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;

/**
 * Describes the workflow for assessment interview panel.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentInterviewPanelStateMachineFactory")
public class InterviewAssignmentWorkflow extends StateMachineConfigurerAdapter<InterviewAssignmentState, InterviewAssignmentEvent> {

    @Autowired
    private NotifyInterviewAssignmentAction notifyInterviewAssignmentAction;

    @Autowired
    private FeedbackResponseInterviewAssignmentAction feedbackResponseInterviewAssignmentAction;

    @Autowired
    private WithdrawResponseInterviewAssignmentAction withdrawResponseInterviewAssignmentAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<InterviewAssignmentState, InterviewAssignmentEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<InterviewAssignmentState, InterviewAssignmentEvent> states) throws Exception {
        states.withStates()
                .initial(CREATED)
                .states(new LinkedHashSet<>(asList(InterviewAssignmentState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<InterviewAssignmentState, InterviewAssignmentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED).target(AWAITING_FEEDBACK_RESPONSE)
                .event(NOTIFY)
                .action(notifyInterviewAssignmentAction)
                .and()
                .withExternal()
                .source(AWAITING_FEEDBACK_RESPONSE).target(AWAITING_FEEDBACK_RESPONSE)
                .event(NOTIFY)
                .and()
                .withExternal()
                .source(AWAITING_FEEDBACK_RESPONSE).target(SUBMITTED_FEEDBACK_RESPONSE)
                .event(RESPOND)
                .action(feedbackResponseInterviewAssignmentAction)
                .and()
                .withExternal()
                .source(SUBMITTED_FEEDBACK_RESPONSE).target(AWAITING_FEEDBACK_RESPONSE)
                .event(WITHDRAW_RESPONSE)
                .action(withdrawResponseInterviewAssignmentAction);
    }
}