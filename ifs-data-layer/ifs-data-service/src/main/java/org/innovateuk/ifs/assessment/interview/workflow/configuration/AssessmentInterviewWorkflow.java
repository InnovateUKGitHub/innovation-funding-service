package org.innovateuk.ifs.assessment.interview.workflow.configuration;

import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewEvent.*;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.*;


/**
 * Describes the workflow for assessment interviews.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentInterviewStateMachineFactory")
public class AssessmentInterviewWorkflow extends StateMachineConfigurerAdapter<AssessmentInterviewState, AssessmentInterviewEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> states) throws Exception {
        states.withStates().initial(CREATED)
                .states(new LinkedHashSet<>(asList(AssessmentInterviewState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> transitions) throws Exception {
        configureNotifyInvitation(transitions);
        configureAcceptInvitation(transitions);
        configureRejectInvitation(transitions);
        configureWithdraw(transitions);
    }

    private void configureNotifyInvitation(StateMachineTransitionConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(NOTIFY)
                .target(PENDING);
    }

    private void configureAcceptInvitation(StateMachineTransitionConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(ACCEPT)
                .target(ACCEPTED)
                .and()
                .withExternal()
                .source(REJECTED)
                .event(ACCEPT)
                .target(ACCEPTED);
    }

    private void configureRejectInvitation(StateMachineTransitionConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(REJECT)
                .target(REJECTED);
    }

    private void configureWithdraw(StateMachineTransitionConfigurer<AssessmentInterviewState, AssessmentInterviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(PENDING)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(REJECTED)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(ACCEPTED)
                .event(WITHDRAW)
                .target(WITHDRAWN);
    }
}