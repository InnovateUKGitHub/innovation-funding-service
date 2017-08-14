package org.innovateuk.ifs.assessment.panel.workflow.configuration;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState;
import org.innovateuk.ifs.assessment.panel.workflow.actions.AssessmentPanelApplicationInviteRejectAction;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteEvent.*;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelApplicationInviteState.*;


/**
 * Describes the workflow for assessment panel application invite.
 */
@Configuration
@EnableStateMachine(name = "assessmentPanelApplicationInviteStateMachine")
public class AssessmentPanelApplicationInviteWorkflow extends StateMachineConfigurerAdapter<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> {

    @Autowired
    private AssessmentPanelApplicationInviteRejectAction rejectAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> states) throws Exception {
        states.withStates().initial(CREATED)
                        .states(new LinkedHashSet<>(asList(AssessmentPanelApplicationInviteState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        configureNotifyInvitation(transitions);
        configureAcceptInvitation(transitions);
        configureRejectInvitation(transitions);
        configureConflictOfInterest(transitions);
    }

    private void configureNotifyInvitation(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(NOTIFY)
                .target(PENDING);
    }

    private void configureAcceptInvitation(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
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

    private void configureRejectInvitation(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(REJECT)
                .target(REJECTED)
                .action(rejectAction);
    }

    private void configureConflictOfInterest(StateMachineTransitionConfigurer<AssessmentPanelApplicationInviteState, AssessmentPanelApplicationInviteEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ACCEPTED)
                .event(MARK_CONFLICT_OF_INTEREST)
                .target(CONFLICT_OF_INTEREST)
                    .and()
                .withExternal()
                .source(CONFLICT_OF_INTEREST)
                .event(UNMARK_CONFLICT_OF_INTEREST)
                .target(ACCEPTED);
    }
}