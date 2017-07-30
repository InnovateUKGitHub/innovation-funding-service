package org.innovateuk.ifs.assessment.panel.workflow.configuration;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent.*;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState.*;


/**
 * Describes the workflow for assessment panel application invite.
 */
@Configuration
@EnableStateMachine(name = "assessmentPanelApplicationInviteStateMachine")
public class AssessmentPanelApplicationInviteWorkflow extends StateMachineConfigurerAdapter<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> states) throws Exception {
        states.withStates().initial(CREATED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        configureNotify(transitions);
        configureAccept(transitions);
        configureReject(transitions);
    }

    private void configureNotify(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(NOTIFY)
                .target(PENDING);
    }

    private void configureAccept(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(ACCEPT)
                .target(ACCEPTED)
                    .and()
                .withExternal()
                .source(REJECTED)
                .event(ACCEPT)
                .target(ACCEPTED);
    }

    private void configureReject(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(REJECT)
                .target(REJECTED);
    }
}