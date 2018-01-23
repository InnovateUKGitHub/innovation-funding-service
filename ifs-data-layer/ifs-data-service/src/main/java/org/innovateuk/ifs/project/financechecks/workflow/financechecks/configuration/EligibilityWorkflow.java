package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.EligibilityEvent.*;
import static org.innovateuk.ifs.project.finance.resource.EligibilityState.*;


/**
 * Describes the workflow for the Eligibility Approval process.
 */
@Configuration
@EnableStateMachineFactory(name = "eligibilityStateMachineFactory")
public class EligibilityWorkflow extends StateMachineConfigurerAdapter<EligibilityState, EligibilityEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<EligibilityState, EligibilityEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<EligibilityState, EligibilityEvent> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, NOT_APPLICABLE, APPROVED))
                .end(EligibilityState.APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EligibilityState, EligibilityEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(REVIEW)
                    .event(PROJECT_CREATED)
                    .target(REVIEW)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(NOT_REQUESTING_FUNDING)
                    .target(NOT_APPLICABLE)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(ELIGIBILITY_APPROVED)
                    .target(APPROVED);
    }
}
