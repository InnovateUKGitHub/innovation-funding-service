package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.domain.AssessmentEvents;
import com.worth.ifs.assessment.domain.AssessmentStates;
import com.worth.ifs.assessment.workflow.actions.AcceptAction;
import com.worth.ifs.assessment.workflow.actions.RecommendAction;
import com.worth.ifs.assessment.workflow.actions.RejectAction;
import com.worth.ifs.assessment.workflow.actions.SubmitAction;
import com.worth.ifs.assessment.workflow.guards.AssessmentGuard;
import com.worth.ifs.assessment.workflow.guards.SubmitGuard;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Describes the workflow for assessment. This is from accepting a competition to submitting the application.
 * A persistent configuration is used, so we can apply different states to different assessments.
 */
@Configuration
@EnableStateMachine
public class AssessorWorkflowConfig extends StateMachineConfigurerAdapter<String, String> {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {

        states.withStates()
                .initial(AssessmentStates.PENDING.getState())
                .states(AssessmentStates.getStates());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(AssessmentStates.PENDING.getState()).target(AssessmentStates.REJECTED.getState())
                    .event(AssessmentEvents.REJECT.getEvent())
                    .action(rejectAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.PENDING.getState()).target(AssessmentStates.OPEN.getState())
                    .event(AssessmentEvents.ACCEPT.getEvent())
                    .action(acceptAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.OPEN.getState()).target(AssessmentStates.ASSESSED.getState())
                    .source(AssessmentStates.ASSESSED.getState()).target(AssessmentStates.ASSESSED.getState())
                    .event(AssessmentEvents.RECOMMEND.getEvent())
                    .action(recommendAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.ASSESSED.getState()).target(AssessmentStates.SUBMITTED.getState())
                    .event(AssessmentEvents.SUBMIT.getEvent())
                    .action(submitAction())
                    .guard(submitGuard());
    }

    @Bean
    public RejectAction rejectAction() {
        return new RejectAction();
    }

    @Bean
    public AcceptAction acceptAction() {
        return new AcceptAction();
    }

    @Bean
    public RecommendAction recommendAction() {
        return new RecommendAction();
    }

    @Bean
    SubmitAction submitAction() {
        return new SubmitAction();
    }

    @Bean
    public AssessmentGuard assessmentExistsGuard() {
        return new AssessmentGuard();
    }

    @Bean
    public SubmitGuard submitGuard() {
        return new SubmitGuard();
    }
}
