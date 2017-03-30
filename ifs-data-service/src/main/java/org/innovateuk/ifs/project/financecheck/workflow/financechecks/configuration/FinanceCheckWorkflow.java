package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes.APPROVE;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckState.APPROVED;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckState.PENDING;

/**
 * Describes the workflow for the Finance Check section for Project Setup, for each Partner Organisation.
 */
@Configuration
@EnableStateMachine(name = "financeCheckStateMachine")
public class FinanceCheckWorkflow extends StateMachineConfigurerAdapter<FinanceCheckState, FinanceCheckOutcomes> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<FinanceCheckState, FinanceCheckOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<FinanceCheckState, FinanceCheckOutcomes> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, APPROVED))
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
                .event(APPROVE)
                .target(APPROVED);
    }
}
