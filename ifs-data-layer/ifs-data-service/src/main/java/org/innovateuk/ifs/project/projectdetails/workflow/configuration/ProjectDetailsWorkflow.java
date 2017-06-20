package org.innovateuk.ifs.project.projectdetails.workflow.configuration;

import org.innovateuk.ifs.project.projectdetails.workflow.guards.AllProjectDetailsSuppliedGuard;
import org.innovateuk.ifs.project.resource.ProjectDetailsOutcomes;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.resource.ProjectDetailsOutcomes.*;
import static org.innovateuk.ifs.project.resource.ProjectDetailsState.*;
import static org.innovateuk.ifs.project.resource.ProjectDetailsState.PENDING;

/**
 * Describes the workflow for the Project Details section for Project Setup.
 */
@Configuration
@EnableStateMachine(name = "projectDetailsStateMachine")
public class ProjectDetailsWorkflow extends StateMachineConfigurerAdapter<ProjectDetailsState, ProjectDetailsOutcomes> {

    @Autowired
    private AllProjectDetailsSuppliedGuard allProjectDetailsSuppliedGuard;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProjectDetailsState, ProjectDetailsOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<ProjectDetailsState, ProjectDetailsOutcomes> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(EnumSet.of(PENDING, READY_TO_SUBMIT, SUBMITTED))
                .choice(DECIDE_IF_READY_TO_SUBMIT)
                .end(SUBMITTED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProjectDetailsState, ProjectDetailsOutcomes> transitions) throws Exception {
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
                .event(PROJECT_MANAGER_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withExternal()
                .source(READY_TO_SUBMIT)
                .event(PROJECT_START_DATE_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withExternal()
                .source(READY_TO_SUBMIT)
                .event(PROJECT_ADDRESS_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withExternal()
                .source(READY_TO_SUBMIT)
                .event(PROJECT_MANAGER_ADDED)
                .target(DECIDE_IF_READY_TO_SUBMIT)
                .and()
            .withChoice()
                .source(DECIDE_IF_READY_TO_SUBMIT)
                .first(READY_TO_SUBMIT, allProjectDetailsSuppliedGuard)
                .last(PENDING)
                .and()
            .withExternal()
                .source(READY_TO_SUBMIT)
                .event(SUBMIT)
                .target(SUBMITTED)
                .guard(allProjectDetailsSuppliedGuard);
    }
}
