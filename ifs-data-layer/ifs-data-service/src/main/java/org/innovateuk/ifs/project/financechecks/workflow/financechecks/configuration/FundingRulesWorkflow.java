package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.FundingRulesEvent;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.FundingRulesEvent.*;
import static org.innovateuk.ifs.project.finance.resource.FundingRulesState.*;


/**
 * Describes the workflow for the Funding Rules Approval process.
 */
@Configuration
@EnableStateMachineFactory(name = "fundingRulesStateMachineFactory")
public class FundingRulesWorkflow extends StateMachineConfigurerAdapter<FundingRulesState, FundingRulesEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<FundingRulesState, FundingRulesEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<FundingRulesState, FundingRulesEvent> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, APPROVED));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<FundingRulesState, FundingRulesEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(REVIEW)
                .event(PROJECT_CREATED)
                .target(REVIEW)
                .and()
                .withExternal()
                .source(REVIEW)
                .event(FUNDING_RULES_UPDATED)
                .target(REVIEW)
                .and()
                .withExternal()
                .source(REVIEW)
                .event(FUNDING_RULES_APPROVED)
                .target(APPROVED)
                .and()
                .withExternal()
                .source(APPROVED)
                .event(FUNDING_RULES_UPDATED)
                .target(REVIEW);
    }
}
