package org.innovateuk.ifs.supporter.workflow;

import org.innovateuk.ifs.supporter.resource.SupporterEvent;
import org.innovateuk.ifs.supporter.resource.SupporterState;
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
@EnableStateMachineFactory(name = "supporterAssignmentStateMachineFactory")
public class SupporterAssignmentWorkflow extends StateMachineConfigurerAdapter<SupporterState, SupporterEvent> {

    @Autowired
    private SupporterDecisionAction supporterDecisionAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<SupporterState, SupporterEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<SupporterState, SupporterEvent> states) throws Exception {
        states.withStates()
                .initial(SupporterState.CREATED)
                .states(new LinkedHashSet<>(asList(SupporterState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SupporterState, SupporterEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(SupporterState.CREATED)
                .target(SupporterState.ACCEPTED)
                .event(SupporterEvent.ACCEPT)
                .action(supporterDecisionAction)
            .and()
                .withExternal()
                .source(SupporterState.ACCEPTED)
                .target(SupporterState.CREATED)
                .event(SupporterEvent.EDIT)
            .and()
                .withExternal()
                .source(SupporterState.CREATED)
                .target(SupporterState.REJECTED)
                .event(SupporterEvent.REJECT)
                .action(supporterDecisionAction)
            .and()
                .withExternal()
                .source(SupporterState.REJECTED)
                .target(SupporterState.CREATED)
                .event(SupporterEvent.EDIT);
    }

}
