package org.innovateuk.ifs.application.workflow.configuration;

import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.actions.AutoCompleteSectionsAction;
import org.innovateuk.ifs.application.workflow.actions.MarkIneligibleAction;
import org.innovateuk.ifs.application.workflow.actions.SendFinanceTotalsAction;
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

/**
 * The workflow for an {@link org.innovateuk.ifs.application.domain.Application}. Describes the possible states and
 * transitions through an Application's lifecycle.
 */
@Configuration
@EnableStateMachineFactory(name = "applicationProcessStateMachineFactory")
public class ApplicationWorkflow extends StateMachineConfigurerAdapter<ApplicationState, ApplicationEvent> {

    @Autowired
    private MarkIneligibleAction markIneligibleAction;

    @Autowired
    private SendFinanceTotalsAction sendFinanceTotalsAction;

    @Autowired
    private AutoCompleteSectionsAction autoCompleteSectionsAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ApplicationState, ApplicationEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ApplicationState, ApplicationEvent> states) throws Exception {
        states.withStates()
                .initial(ApplicationState.CREATED)
                .states(new LinkedHashSet<>(asList(ApplicationState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        configureOpen(transitions);
        configureSubmit(transitions);
        configureReopen(transitions);
        configureIneligible(transitions);
        configureReject(transitions);
        configureApprove(transitions);
    }

    private void configureOpen(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.CREATED)
                .event(ApplicationEvent.OPEN)
                .action(autoCompleteSectionsAction)
                .target(ApplicationState.OPENED);
    }

    private void configureSubmit(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.OPENED)
                .event(ApplicationEvent.SUBMIT)
                .action(sendFinanceTotalsAction)
                .target(ApplicationState.SUBMITTED);
    }


    private void configureReopen(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.SUBMITTED)
                .event(ApplicationEvent.REOPEN)
                .target(ApplicationState.OPENED);
    }

    private void configureIneligible(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.SUBMITTED)
                .event(ApplicationEvent.MARK_INELIGIBLE)
                .action(markIneligibleAction)
                .target(ApplicationState.INELIGIBLE)
                .and()
                .withExternal()
                .source(ApplicationState.INELIGIBLE)
                .event(ApplicationEvent.INFORM_INELIGIBLE)
                .target(ApplicationState.INELIGIBLE_INFORMED)
                .and()
                .withExternal()
                .source(ApplicationState.INELIGIBLE)
                .event(ApplicationEvent.REINSTATE_INELIGIBLE)
                .target(ApplicationState.SUBMITTED)
                .and()
                .withExternal()
                .source(ApplicationState.INELIGIBLE_INFORMED)
                .event(ApplicationEvent.REINSTATE_INELIGIBLE)
                .target(ApplicationState.SUBMITTED);

    }

    private void configureReject(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.SUBMITTED)
                .event(ApplicationEvent.REJECT)
                .target(ApplicationState.REJECTED);
    }


    private void configureApprove(StateMachineTransitionConfigurer<ApplicationState, ApplicationEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ApplicationState.SUBMITTED)
                .event(ApplicationEvent.APPROVE)
                .target(ApplicationState.APPROVED)
                .and()
                .withExternal()
                .source(ApplicationState.REJECTED)
                .event(ApplicationEvent.APPROVE)
                .target(ApplicationState.APPROVED)
                .and()
                .withExternal()
                .source(ApplicationState.INELIGIBLE)
                .event(ApplicationEvent.APPROVE)
                .target(ApplicationState.APPROVED)
                .and()
                .withExternal()
                .source(ApplicationState.INELIGIBLE_INFORMED)
                .event(ApplicationEvent.APPROVE)
                .target(ApplicationState.APPROVED);

    }

}
