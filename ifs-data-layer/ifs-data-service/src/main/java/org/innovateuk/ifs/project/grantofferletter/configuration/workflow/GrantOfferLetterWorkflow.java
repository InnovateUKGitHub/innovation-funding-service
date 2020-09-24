package org.innovateuk.ifs.project.grantofferletter.configuration.workflow;

import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;

/**
 * Describes the workflow for the GOL section for Project Setup.
 */
@Configuration
@EnableStateMachineFactory(name = "golStateMachineFactory")
public class GrantOfferLetterWorkflow extends StateMachineConfigurerAdapter<GrantOfferLetterState, GrantOfferLetterEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<GrantOfferLetterState, GrantOfferLetterEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<GrantOfferLetterState, GrantOfferLetterEvent> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, SENT, READY_TO_APPROVE, APPROVED))
                .end(APPROVED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<GrantOfferLetterState, GrantOfferLetterEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(PENDING)
                    .event(PROJECT_CREATED)
                    .target(PENDING)
                    .and()
                .withExternal()
                    .source(PENDING)
                    .event(GOL_REMOVED)
                    .target(PENDING)
                    .and()
                .withExternal()
                    .source(PENDING)
                    .event(GOL_SENT)
                    .target(SENT)
                    .and()
                .withExternal()
                    .source(SENT)
                    .event(GOL_RESET)
                    .target(PENDING)
                    .and()
                .withExternal()
                    .source(SENT)
                    .event(GOL_SIGNED)
                    .target(READY_TO_APPROVE)
                    .and()
                .withExternal()
                    .source(READY_TO_APPROVE)
                    .event(SIGNED_GOL_REJECTED)
                    .target(SENT)
                    .and()
                .withExternal()
                    .source(SENT)
                    .event(SIGNED_GOL_REMOVED)
                    .target(SENT)
                    .and()
                .withExternal()
                    .source(READY_TO_APPROVE)
                    .event(SIGNED_GOL_APPROVED)
                    .target(APPROVED);
    }
}
