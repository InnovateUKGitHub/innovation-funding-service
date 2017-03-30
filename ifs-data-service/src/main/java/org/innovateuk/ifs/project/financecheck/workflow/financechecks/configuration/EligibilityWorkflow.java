package org.innovateuk.ifs.project.financecheck.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.ELIGIBILITY_APPROVED;
import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.NOT_REQUESTING_FUNDING;
import static org.innovateuk.ifs.project.finance.resource.EligibilityOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.finance.resource.EligibilityState.REVIEW;
import static org.innovateuk.ifs.project.finance.resource.EligibilityState.NOT_APPLICABLE;
import static org.innovateuk.ifs.project.finance.resource.EligibilityState.APPROVED;


/**
 * Describes the workflow for the Eligibility Approval process.
 */
@Configuration
@EnableStateMachine(name = "eligibilityStateMachine")
public class EligibilityWorkflow extends StateMachineConfigurerAdapter<EligibilityState, EligibilityOutcomes> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<EligibilityState, EligibilityOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<EligibilityState, EligibilityOutcomes> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, NOT_APPLICABLE, APPROVED))
                .end(EligibilityState.APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EligibilityState, EligibilityOutcomes> transitions) throws Exception {
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
