package org.innovateuk.ifs.project.spendprofile.configuration.workflow;

import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent.*;
import static org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState.*;

/**
 * Describes the workflow for the overall project Spend Profile section for Project Setup.
 */
@Configuration
@EnableStateMachine(name = "spendProfileStateMachine")
public class SpendProfileWorkflow extends StateMachineConfigurerAdapter<SpendProfileState, SpendProfileEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<SpendProfileState, SpendProfileEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<SpendProfileState, SpendProfileEvent> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, CREATED, SUBMITTED, APPROVED, REJECTED))
                .end(APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SpendProfileState, SpendProfileEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(PROJECT_CREATED)
                .target(PENDING)
                .and()
                .withExternal()
                .source(PENDING)
                .event(SPEND_PROFILE_GENERATED)
                .target(CREATED)
                .and()
                .withExternal()
                .source(CREATED)
                .event(SPEND_PROFILE_SUBMITTED)
                .target(SUBMITTED)
                .and()
                .withExternal()
                .source(SUBMITTED)
                .event(SPEND_PROFILE_APPROVED)
                .target(APPROVED)
                .and()
                .withExternal()
                .source(SUBMITTED)
                .event(SPEND_PROFILE_REJECTED)
                .target(REJECTED)
                .and()
                .withExternal()
                .source(REJECTED)
                .event(SPEND_PROFILE_SUBMITTED)
                .target(SUBMITTED);
    }
}
