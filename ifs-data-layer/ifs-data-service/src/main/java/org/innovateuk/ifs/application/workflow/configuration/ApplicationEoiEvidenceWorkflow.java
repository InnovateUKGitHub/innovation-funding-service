package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceEvent;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "applicationEoiEvidenceProcessStateMachineFactory")
public class ApplicationEoiEvidenceWorkflow extends StateMachineConfigurerAdapter<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent>  {

    @Override
    public void configure(StateMachineConfigurationConfigurer<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent> states) throws Exception {
        states.withStates()
                .initial(ApplicationEoiEvidenceState.CREATED)
                .states(EnumSet.of(ApplicationEoiEvidenceState.CREATED, ApplicationEoiEvidenceState.NOT_SUBMITTED, ApplicationEoiEvidenceState.SUBMITTED));
    }
    @Override
    public void configure(StateMachineTransitionConfigurer<ApplicationEoiEvidenceState, ApplicationEoiEvidenceEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationEoiEvidenceState.CREATED)
                .event(ApplicationEoiEvidenceEvent.UNSUBMIT)
                .target(ApplicationEoiEvidenceState.NOT_SUBMITTED)
                .and()
                .withExternal()
                .source(ApplicationEoiEvidenceState.NOT_SUBMITTED)
                .event(ApplicationEoiEvidenceEvent.SUBMIT)
                .target(ApplicationEoiEvidenceState.SUBMITTED);
    }
}
