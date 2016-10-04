package com.worth.ifs.project.finance.workflow.financechecks.configuration;

import com.worth.ifs.project.finance.resource.FinanceCheckOutcomes;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.finance.workflow.financechecks.guards.AllFinanceCheckFiguresEnteredGuard;
import com.worth.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.worth.ifs.project.finance.resource.FinanceCheckOutcomes.APPROVE;
import static com.worth.ifs.project.finance.resource.FinanceCheckOutcomes.FINANCE_CHECK_FIGURES_EDITED;
import static com.worth.ifs.project.finance.resource.FinanceCheckOutcomes.PROJECT_CREATED;
import static com.worth.ifs.project.finance.resource.FinanceCheckState.*;

/**
 * Describes the workflow for the Finance Check section for Project Setup, for each Partner Organisation.
 */
@Configuration
@EnableStateMachine(name = "financeCheckStateMachine")
public class FinanceCheckWorkflow extends StateMachineConfigurerAdapter<FinanceCheckState, FinanceCheckOutcomes> {

    @Autowired
    private AllFinanceCheckFiguresEnteredGuard allFinanceCheckFiguresEnteredGuard;

    @Override
    public void configure(StateMachineConfigurationConfigurer<FinanceCheckState, FinanceCheckOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<FinanceCheckState, FinanceCheckOutcomes> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, READY_TO_APPROVE, APPROVED))
                .choice(DECIDE_IF_READY_TO_APPROVE)
                .end(APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<FinanceCheckState, FinanceCheckOutcomes> transitions) throws Exception {
        transitions
            .withExternal()
                .source(PENDING)
                .event(PROJECT_CREATED)
                .target(PENDING)
                .and()
            .withExternal()
                .source(PENDING)
                .event(FINANCE_CHECK_FIGURES_EDITED)
                .target(DECIDE_IF_READY_TO_APPROVE)
                .and()
            .withExternal()
                .source(READY_TO_APPROVE)
                .event(FINANCE_CHECK_FIGURES_EDITED)
                .target(DECIDE_IF_READY_TO_APPROVE)
                .and()
            .withChoice()
                .source(DECIDE_IF_READY_TO_APPROVE)
                .first(READY_TO_APPROVE, allFinanceCheckFiguresEnteredGuard)
                .last(PENDING)
                .and()
            .withExternal()
                .source(READY_TO_APPROVE)
                .event(APPROVE)
                .target(APPROVED)
                .guard(allFinanceCheckFiguresEnteredGuard);
    }
}
