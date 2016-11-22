package com.worth.ifs.project.gol.workflow.configuration;

import com.worth.ifs.project.gol.resource.GOLOutcomes;
import com.worth.ifs.project.gol.resource.GOLState;
import com.worth.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.worth.ifs.project.gol.resource.GOLState.*;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.PROJECT_CREATED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_SENT;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_SIGNED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_APPROVED;
import static com.worth.ifs.project.gol.resource.GOLOutcomes.GOL_REJECTED;


import java.util.EnumSet;

/**
 * Describes the workflow for the GOL section for Project Setup.
 */
@Configuration
@EnableStateMachine(name = "golStateMachine")
public class GOLWorkflow extends StateMachineConfigurerAdapter<GOLState, GOLOutcomes> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<GOLState, GOLOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<GOLState, GOLOutcomes> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, SENT, READY_TO_APPROVE, APPROVED))
                .end(APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<GOLState, GOLOutcomes> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PENDING)
                    .event(PROJECT_CREATED)
                    .target(PENDING)
                    .and()
                .withExternal()
                    .source(PENDING)
                    .event(GOL_SENT)
                    .target(SENT)
                    .and()
                .withExternal()
                    .source(SENT)
                    .event(GOL_SIGNED)
                    .target(READY_TO_APPROVE)
                    .and()
                .withExternal()
                    .source(READY_TO_APPROVE)
                    .event(GOL_REJECTED)
                    .target(PENDING)
                    .and()
                .withExternal()
                    .source(READY_TO_APPROVE)
                    .event(GOL_APPROVED)
                    .target(APPROVED);
    }
}
