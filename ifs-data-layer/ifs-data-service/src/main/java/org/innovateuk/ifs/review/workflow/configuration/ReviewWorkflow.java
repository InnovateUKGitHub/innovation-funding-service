package org.innovateuk.ifs.review.workflow.configuration;

import org.innovateuk.ifs.review.resource.ReviewEvent;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.review.workflow.actions.ReviewRejectAction;
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
import static org.innovateuk.ifs.review.resource.ReviewEvent.*;
import static org.innovateuk.ifs.review.resource.ReviewState.*;


/**
 * Describes the workflow for assessment reviews.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentReviewStateMachineFactory")
public class ReviewWorkflow extends StateMachineConfigurerAdapter<ReviewState, ReviewEvent> {

    @Autowired
    private ReviewRejectAction rejectAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ReviewState, ReviewEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ReviewState, ReviewEvent> states) throws Exception {
        states.withStates().initial(CREATED)
                .states(new LinkedHashSet<>(asList(ReviewState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
        configureNotifyInvitation(transitions);
        configureAcceptInvitation(transitions);
        configureRejectInvitation(transitions);
        configureConflictOfInterest(transitions);
        configureWithdraw(transitions);
    }

    private void configureNotifyInvitation(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED)
                .event(NOTIFY)
                .target(PENDING);
    }

    private void configureAcceptInvitation(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
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

    private void configureRejectInvitation(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PENDING)
                .event(REJECT)
                .target(REJECTED)
                .action(rejectAction);
    }

    private void configureConflictOfInterest(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
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

    private void configureWithdraw(StateMachineTransitionConfigurer<ReviewState, ReviewEvent> transitions) throws Exception {
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