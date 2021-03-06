package org.innovateuk.ifs.project.projectdetails.workflow.configuration;

import org.innovateuk.ifs.project.projectdetails.workflow.actions.ProjectDetailsCompleteAction;
import org.innovateuk.ifs.project.projectdetails.workflow.guards.AllProjectDetailsSuppliedGuard;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.resource.ProjectDetailsEvent.*;
import static org.innovateuk.ifs.project.resource.ProjectDetailsState.*;

/**
 * Describes the workflow for the Project Details section for Project Setup.
 */
@Configuration
@EnableStateMachineFactory(name = "projectDetailsStateMachineFactory")
public class ProjectDetailsWorkflow extends StateMachineConfigurerAdapter<ProjectDetailsState, ProjectDetailsEvent> {

    @Autowired
    private AllProjectDetailsSuppliedGuard allProjectDetailsSuppliedGuard;

    @Autowired
    private ProjectDetailsCompleteAction projectDetailsCompleteAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProjectDetailsState, ProjectDetailsEvent> config) throws Exception {
        config.withConfiguration()
                .listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ProjectDetailsState, ProjectDetailsEvent> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, SUBMITTED))
                .choice(DECIDE_IF_READY_TO_SUBMIT)
                .end(SUBMITTED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProjectDetailsState, ProjectDetailsEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(PENDING)
                .event(PROJECT_CREATED)
                .target(PENDING)
                .and()
            .withExternal()
                .source(PENDING)
                .event(PROJECT_START_DATE_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withExternal()
                .source(PENDING)
                .event(PROJECT_ADDRESS_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withExternal()
                .source(PENDING)
                .event(PROJECT_LOCATION_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withChoice()
                .source(DECIDE_IF_READY_TO_SUBMIT)
                .first(SUBMITTED, allProjectDetailsSuppliedGuard, projectDetailsCompleteAction)
                .last(PENDING);

    }
}
