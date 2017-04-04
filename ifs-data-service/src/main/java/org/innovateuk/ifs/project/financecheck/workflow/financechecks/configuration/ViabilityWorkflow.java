package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.ORGANISATION_IS_ACADEMIC;
import static org.innovateuk.ifs.project.finance.resource.ViabilityOutcomes.VIABILITY_APPROVED;

import static org.innovateuk.ifs.project.finance.resource.ViabilityState.REVIEW;
import static org.innovateuk.ifs.project.finance.resource.ViabilityState.NOT_APPLICABLE;
import static org.innovateuk.ifs.project.finance.resource.ViabilityState.APPROVED;

/**
 * Describes the workflow for the Viability Approval process.
 */
@Configuration
@EnableStateMachine(name = "viabilityStateMachine")
public class ViabilityWorkflow extends StateMachineConfigurerAdapter<ViabilityState, ViabilityOutcomes> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<ViabilityState, ViabilityOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<ViabilityState, ViabilityOutcomes> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, NOT_APPLICABLE, APPROVED))
                .end(APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ViabilityState, ViabilityOutcomes> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(REVIEW)
                    .event(PROJECT_CREATED)
                    .target(REVIEW)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(ORGANISATION_IS_ACADEMIC)
                    .target(NOT_APPLICABLE)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(VIABILITY_APPROVED)
                    .target(APPROVED);
    }
}
