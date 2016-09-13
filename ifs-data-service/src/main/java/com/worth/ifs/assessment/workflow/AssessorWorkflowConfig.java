package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.actions.AcceptAction;
import com.worth.ifs.assessment.workflow.actions.RecommendAction;
import com.worth.ifs.assessment.workflow.actions.RejectAction;
import com.worth.ifs.assessment.workflow.actions.SubmitAction;
import com.worth.ifs.assessment.workflow.guards.AssessmentGuard;
import com.worth.ifs.assessment.workflow.guards.ProcessOutcomeGuard;
import com.worth.ifs.assessment.workflow.guards.SubmitGuard;
import com.worth.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

/**
 * Describes the workflow for assessment. This is from accepting a competition to submitting the application.
 * A persistent configuration is used, so we can apply different states to different assessments.
 */
@Configuration
@EnableStateMachine
public class AssessorWorkflowConfig extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener());

    }

    @Override
    public void configure(StateMachineStateConfigurer<String, String> states) throws Exception {
        states.withStates()
                .initial(AssessmentStates.PENDING.getStateName())
                .states(AssessmentStates.getStates());
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<String, String> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(AssessmentStates.PENDING.getStateName()).target(AssessmentStates.REJECTED.getStateName())
                    .event(AssessmentOutcomes.REJECT.getType())
                    .action(rejectAction())
                    .guard(processOutcomeExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.PENDING.getStateName()).target(AssessmentStates.OPEN.getStateName())
                    .event(AssessmentOutcomes.ACCEPT.getType())
                    .action(acceptAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.OPEN.getStateName()).target(AssessmentStates.ASSESSED.getStateName())
                    .event(AssessmentOutcomes.RECOMMEND.getType())
                    .action(recommendAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.OPEN.getStateName()).target(AssessmentStates.REJECTED.getStateName())
                    .event(AssessmentOutcomes.REJECT.getType())
                    .action(rejectAction())
                    .guard(processOutcomeExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.ASSESSED.getStateName()).target(AssessmentStates.ASSESSED.getStateName())
                    .event(AssessmentOutcomes.RECOMMEND.getType())
                    .action(recommendAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                     .source(AssessmentStates.ASSESSED.getStateName()).target(AssessmentStates.REJECTED.getStateName())
                     .event(AssessmentOutcomes.REJECT.getType())
                     .action(rejectAction())
                     .guard(processOutcomeExistsGuard())
                     .and()
                .withExternal()
                    .source(AssessmentStates.ASSESSED.getStateName()).target(AssessmentStates.SUBMITTED.getStateName())
                    .event(AssessmentOutcomes.SUBMIT.getType())
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
    public ProcessOutcomeGuard processOutcomeExistsGuard() {
        return new ProcessOutcomeGuard();
    }

    @Bean
    public SubmitGuard submitGuard() {
        return new SubmitGuard();
    }

}
