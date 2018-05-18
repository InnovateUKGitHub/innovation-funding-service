package org.innovateuk.ifs.interview.workflow.configuration;

import org.innovateuk.ifs.interview.resource.InterviewEvent;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.interview.resource.InterviewEvent.NOTIFY;
import static org.innovateuk.ifs.interview.resource.InterviewState.ASSIGNED;

/**
 * Describes the workflow for assessment interviews.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentInterviewStateMachineFactory")
public class InterviewWorkflow extends StateMachineConfigurerAdapter<InterviewState, InterviewEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<InterviewState, InterviewEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<InterviewState, InterviewEvent> states) throws Exception {
        states.withStates().initial(ASSIGNED)
                .states(new LinkedHashSet<>(asList(InterviewState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<InterviewState, InterviewEvent> transitions) throws Exception {
        configureNotifyInvitation(transitions);
    }

    private void configureNotifyInvitation(StateMachineTransitionConfigurer<InterviewState, InterviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ASSIGNED)
                .event(NOTIFY)
                .target(ASSIGNED); // TODO this might now be pointless
    }
}