package org.innovateuk.ifs.assessment.panel.workflow.configuration;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewEvent;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.assessment.panel.workflow.actions.AssessmentReviewRejectAction;
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
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewEvent.*;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.*;


/**
 * Describes the workflow for assessment reviews.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentReviewStateMachineFactory")
public class AssessmentReviewWorkflow extends StateMachineConfigurerAdapter<AssessmentReviewState, AssessmentReviewEvent> {

    @Autowired
    private AssessmentReviewRejectAction rejectAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentReviewState, AssessmentReviewEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentReviewState, AssessmentReviewEvent> states) throws Exception {
        states.withStates().initial(CREATED)
                .states(new LinkedHashSet<>(asList(AssessmentReviewState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
        configureNotifyInvitation(transitions);
        configureAcceptInvitation(transitions);
        configureRejectInvitation(transitions);
        configureConflictOfInterest(transitions);
        configureWithdraw(transitions);
    }

    private void configureNotifyInvitation(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(NOTIFY)
                .target(PENDING);
    }

    private void configureAcceptInvitation(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
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

    private void configureRejectInvitation(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(REJECT)
                .target(REJECTED)
                .action(rejectAction);
    }

    private void configureConflictOfInterest(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
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

    private void configureWithdraw(StateMachineTransitionConfigurer<AssessmentReviewState, AssessmentReviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(PENDING)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(REJECTED)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(ACCEPTED)
                .event(WITHDRAW)
                .target(WITHDRAWN)
                .and()
                .withExternal()
                .source(CONFLICT_OF_INTEREST)
                .event(WITHDRAW)
                .target(WITHDRAWN);
    }
}