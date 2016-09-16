package com.worth.ifs.assessment.workflow.configuration;

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

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;

/**
 * Describes the workflow for assessment. This is from accepting a competition to submitting the application.
 * A persistent configuration is used, so we can apply different states to different assessments.
 */
@Configuration
@EnableStateMachine(name = "assessmentStateMachine")
public class AssessmentWorkflow extends StateMachineConfigurerAdapter<AssessmentStates, String> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentStates, String> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener());

    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentStates, String> states) throws Exception {
        states.withStates()
                .initial(AssessmentStates.PENDING)
                .states(new LinkedHashSet<>(asList(AssessmentStates.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentStates, String> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(AssessmentStates.PENDING).target(AssessmentStates.REJECTED)
                    .event(AssessmentOutcomes.REJECT.getType())
                    .action(rejectAction())
                    .guard(processOutcomeExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.PENDING).target(AssessmentStates.OPEN)
                    .event(AssessmentOutcomes.ACCEPT.getType())
                    .action(acceptAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.OPEN).target(AssessmentStates.ASSESSED)
                    .event(AssessmentOutcomes.RECOMMEND.getType())
                    .action(recommendAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.OPEN).target(AssessmentStates.REJECTED)
                    .event(AssessmentOutcomes.REJECT.getType())
                    .action(rejectAction())
                    .guard(processOutcomeExistsGuard())
                    .and()
                .withExternal()
                    .source(AssessmentStates.ASSESSED).target(AssessmentStates.ASSESSED)
                    .event(AssessmentOutcomes.RECOMMEND.getType())
                    .action(recommendAction())
                    .guard(assessmentExistsGuard())
                    .and()
                .withExternal()
                     .source(AssessmentStates.ASSESSED).target(AssessmentStates.REJECTED)
                     .event(AssessmentOutcomes.REJECT.getType())
                     .action(rejectAction())
                     .guard(processOutcomeExistsGuard())
                     .and()
                .withExternal()
                    .source(AssessmentStates.ASSESSED).target(AssessmentStates.SUBMITTED)
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
