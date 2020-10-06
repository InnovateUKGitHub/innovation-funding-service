package org.innovateuk.ifs.cofunder.workflow;

import org.innovateuk.ifs.cofunder.resource.CofunderEvent;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;

@Configuration
@EnableStateMachineFactory(name = "cofunderAssignmentStateMachineFactory")
public class CofunderAssignmentWorkflow extends StateMachineConfigurerAdapter<CofunderState, CofunderEvent> {

    @Autowired
    private CofunderDecisionAction cofunderDecisionAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<CofunderState, CofunderEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<CofunderState, CofunderEvent> states) throws Exception {
        states.withStates()
                .initial(CofunderState.CREATED)
                .states(new LinkedHashSet<>(asList(CofunderState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CofunderState, CofunderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CofunderState.CREATED)
                .target(CofunderState.ACCEPTED)
                .event(CofunderEvent.ACCEPT)
                .action(cofunderDecisionAction)
            .and()
                .withExternal()
                .source(CofunderState.ACCEPTED)
                .target(CofunderState.CREATED)
                .event(CofunderEvent.EDIT)
            .and()
                .withExternal()
                .source(CofunderState.CREATED)
                .target(CofunderState.REJECTED)
                .event(CofunderEvent.REJECT)
                .action(cofunderDecisionAction)
            .and()
                .withExternal()
                .source(CofunderState.REJECTED)
                .target(CofunderState.CREATED)
                .event(CofunderEvent.EDIT);
    }

}
